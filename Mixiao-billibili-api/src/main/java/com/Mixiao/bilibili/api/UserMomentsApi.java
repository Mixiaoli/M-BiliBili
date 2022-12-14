package com.Mixiao.bilibili.api;

import com.Mixiao.bilibili.api.support.UserSupport;
import com.Mixiao.bilibili.domain.JsonResponse;
import com.Mixiao.bilibili.domain.UserMoment;
import com.Mixiao.bilibili.domain.annotation.ApiLimitedRole;
import com.Mixiao.bilibili.domain.annotation.DataLimited;
import com.Mixiao.bilibili.domain.constant.AuthRoleConstant;
import com.Mixiao.bilibili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;


    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})//使用aop 权限
    @DataLimited//数据库权限控制 用来判断字段传进来是否正确
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }
    //查询订阅动态
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
