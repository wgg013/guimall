package com.gg.guimall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.gg.guimall.*"})//多模块项目中，必需手动指定扫描com.gg.guimall包下面的所有类
public class GuimallWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuimallWebApplication.class,args);
    }
}
