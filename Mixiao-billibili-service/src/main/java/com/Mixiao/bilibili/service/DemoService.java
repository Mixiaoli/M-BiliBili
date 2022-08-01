package com.Mixiao.bilibili.service;

import com.Mixiao.bilibili.dao.DemoDao;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DemoService {
    @Autowired
    private DemoDao demoDao;
    public Long query(Long id){
        return demoDao.query(id);
    }
}
