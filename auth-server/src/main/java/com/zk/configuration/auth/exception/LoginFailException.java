package com.zk.configuration.auth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = LoginFailExceptionSerializer.class)
public class LoginFailException extends OAuth2Exception {
    public LoginFailException(String msg) {
        super(msg);
    }

    public LoginFailException(Throwable realException) {
        super(realException.getMessage());
        this.realException = realException;
    }

    private Throwable realException;

    public Throwable getRealException() {
        return realException;
    }

    public void setRealException(Throwable realException) {
        this.realException = realException;
    }
}