package com.zk.auth.captch.exception;

import org.springframework.security.core.AuthenticationException;

public class ImageCodeException extends AuthenticationException {
    private Integer code;
    private String message;

    public ImageCodeException(String msg) {
        super(msg);
        this.message = msg;
    }


    public ImageCodeException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
