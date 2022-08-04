package com.Mixiao.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)//运行阶段中执行
@Target({ElementType.METHOD})//代理对象 目标 就是在方法中
@Documented// java 在生成文档,是否显示注解的开关
@Component//注入
public @interface ApiLimitedRole {

    String[] limitedRoleCodeList() default {};
}
