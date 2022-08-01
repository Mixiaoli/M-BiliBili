package com.Mixiao.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JsonHttpMessageConverterConfig {
    //这里是演示循环引用 是什么 很重要 一定要关闭循环引用
    public static void main(String[] args){
        List<Object> list = new ArrayList<>();
        Object o = new Object();
        list.add(o);
        list.add(o);
        System.out.println(list.size());
        System.out.println(JSONObject.toJSONString(list));
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));
    }
    //json转换配置类
    @Bean
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,//格式化输出
                SerializerFeature.WriteNullStringAsEmpty,//null转换为空字符串 返回到前端
                SerializerFeature.WriteNullListAsEmpty,//列表转换空
                SerializerFeature.WriteMapNullValue,//map为null的转换
                SerializerFeature.MapSortField,//map相关字段 key:value 做排序
                SerializerFeature.DisableCircularReferenceDetect//禁用循环引用
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);//config反到converter里
        return new HttpMessageConverters(fastConverter);//return 传到构造器里来
    }
}
