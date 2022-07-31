package com.Mixiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MixiaoBilibiliApp {
    public static void main(String[] args){
        ApplicationContext app = SpringApplication.run(MixiaoBilibiliApp.class,args);
    }
}
