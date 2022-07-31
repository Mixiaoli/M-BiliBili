package com.Mixiao.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper //自动匹配封装为一个实体类mybatis
public interface DemoDao {
    public Long query(Long id);
}
