package com.gg.guimall.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/*
* @author:wly
* @url:www.gg.com
* @date:2025/1/28
* @description:用户名或者密码为异常*/
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}


