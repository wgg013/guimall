package com.gg.guimall.web;


import com.gg.guimall.common.domain.dos.UserDO;
import com.gg.guimall.common.domain.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
class GuimallWebApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testLog() {
        log.info("这是一行 Info 级别日志");
        log.warn("这是一行 Warn 级别日志");
        log.error("这是一行 Error 级别日志");

        // 占位符
        String author = "王琳媛";
        log.info("这是一行带有占位符日志，作者：{}", author);
    }

    @Test
    void insertTest() {
        // 构建数据库实体类
        UserDO userDO = UserDO.builder()
                .username("王琳媛")
                .password("123456")
                .avatar("https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/45cc3e8bf79f46f9f43c6111c80dc315.png?thumb=1&w=1736&h=651&f=webp&q=90?")
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();

        userMapper.insert(userDO);
    }

}

