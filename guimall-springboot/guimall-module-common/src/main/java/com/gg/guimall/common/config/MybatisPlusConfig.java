package com.gg.guimall.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/*
* @author:wly
* @url:www.gg.com
* @date:2026/1/24
* @description:Mybatis Plus 配置文件*/
@Configuration
@MapperScan("com.gg.guimall.common.domain.mapper")
public class MybatisPlusConfig {
}
