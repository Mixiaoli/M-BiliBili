package com.Mixiao.bilibili.service;

import com.Mixiao.bilibili.dao.UserDao;
import com.Mixiao.bilibili.domain.User;
import com.Mixiao.bilibili.domain.UserInfo;
import com.Mixiao.bilibili.domain.constant.UserConstant;
import com.Mixiao.bilibili.domain.exception.ConditionException;
import com.Mixiao.bilibili.service.util.MD5Util;
import com.Mixiao.bilibili.service.util.RSAUtil;
import com.Mixiao.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public void addUser(User user){
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空!");
        }
        User dbUser = this.getUserByPhone(phone);
        if(dbUser != null){
            throw new ConditionException("该手机号已经注册！");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
        try{
            rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new ConditionException("密码解密失败");
        }
        String md5Password = MD5Util.sign(rawPassword,salt,"UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        //添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
    }

    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) throws Exception{
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空!");
        }
        User dbUser = this.getUserByPhone(phone); //根据查询电话数据 拿到全部信息
        if (dbUser == null){
            throw new ConditionException("当前用户不存在!");
        }
        String password = user.getPassword();
        String rawPassword;
        try{
            rawPassword = RSAUtil.decrypt(password);//RSA解密
        }catch (Exception e){
            throw new ConditionException("密码解密失败！");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword,salt,"UTF-8");//拿到加密后的md5密码
        if (!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误！");
        }
        //生成token
        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
        User user= userDao.getUserById(userId);
        //查询用户信息
        UserInfo userInfo =userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }
}
