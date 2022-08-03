package com.Mixiao.bilibili.service;

import com.Mixiao.bilibili.dao.UserFollowingDao;
import com.Mixiao.bilibili.domain.FollowingGroup;
import com.Mixiao.bilibili.domain.User;
import com.Mixiao.bilibili.domain.UserFollowing;
import com.Mixiao.bilibili.domain.UserInfo;
import com.Mixiao.bilibili.domain.constant.UserConstant;
import com.Mixiao.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;
    //分组
    public void addUserFollowings(UserFollowing userFollowing){
        Long groupId = userFollowing.getGroupId();
        if (groupId==null){
            //默认分组
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else {
            FollowingGroup followingGroup =followingGroupService.getById(groupId);
            if (followingGroup == null){
                throw new ConditionException("关注的分组不存在！");
            }
        }
        Long followingId = userFollowing.getFollowingId();
        User user=userService.getUserById(followingId);
        if (user == null){
            throw new ConditionException("关注用户不存在！");
        }
        //新增用户关注
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(),followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }
    //第一步 获取关注的用户列表
    //第二步 根据关注用户的id查询关注用户的基本信息
    //第三步 将关注的用户按关注分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId){
        List<UserFollowing> list =  userFollowingDao.getUserFollowings(userId);
        //把查询到的id 分为列表
        Set<Long> followingIdSet =list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if(followingIdSet.size() > 0){
           userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        for (UserFollowing userFollowing :list){
            for (UserInfo userInfo :userInfoList){
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        //全部关注分组
        FollowingGroup allGroup =  new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        for (FollowingGroup group : groupList){
            List<UserInfo> infoList =new ArrayList<>();
            for (UserFollowing userFollowing : list){
                if (group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }
    //获取用户粉丝列表
    //根据粉丝的用户id查询基本信息
    //查询当前用户是否已经关注了该粉丝
    public List<UserFollowing> getUserFans(Long userId){
        List<UserFollowing> fanList =  userFollowingDao.getUserFans(userId);//获取用户粉丝
        //把粉丝的id抽取出来
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (fanIdSet.size() >0){
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for(UserFollowing fan : fanList){
            for(UserInfo userInfo : userInfoList){
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            for(UserFollowing following : followingList){
                if(following.getFollowingId().equals(fan.getUserId())){
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }
    //添加分组
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }
    //获取分组
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for(UserInfo userInfo : userInfoList){
            userInfo.setFollowed(false);
            for(UserFollowing userFollowing : userFollowingList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
