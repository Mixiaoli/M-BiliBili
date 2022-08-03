package com.Mixiao.bilibili.dao;

import com.Mixiao.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRoleDao {

    AuthRole getRoleByCode(String code);
}
