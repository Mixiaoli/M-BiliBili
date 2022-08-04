package com.Mixiao.bilibili.api.aspect;

import com.Mixiao.bilibili.api.support.UserSupport;
import com.Mixiao.bilibili.domain.UserMoment;
import com.Mixiao.bilibili.domain.annotation.ApiLimitedRole;
import com.Mixiao.bilibili.domain.auth.UserRole;
import com.Mixiao.bilibili.domain.constant.AuthRoleConstant;
import com.Mixiao.bilibili.domain.exception.ConditionException;
import com.Mixiao.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.Mixiao.bilibili.domain.annotation.DataLimited)")
    public void check(){
    }
    //数据库权限控制 用来判断字段传进来是否正确
    @Before("check()")
    public void doBefore(JoinPoint joinPoint){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        for(Object arg : args){
          if(arg instanceof UserMoment){
              UserMoment userMoment = (UserMoment)arg;
              String type = userMoment.getType();
              if(roleCodeSet.contains(AuthRoleConstant.ROLE_LV1) && !"0".equals(type)){
                  throw new ConditionException("参数异常");
              }
          }
        }
    }
}
