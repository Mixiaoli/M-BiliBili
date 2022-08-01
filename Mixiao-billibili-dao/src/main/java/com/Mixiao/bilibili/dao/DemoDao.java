package com.Mixiao.bilibili.dao;

import com.sun.javafx.collections.MappingChange;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper //自动匹配封装为一个实体类mybatis
public interface DemoDao {
    public Long query(Long id);
}
