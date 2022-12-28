package com.zk.configuration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

@Component
public class LoginWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity<LoginFailException> translate(Exception e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new LoginFailException(e));
    }
}