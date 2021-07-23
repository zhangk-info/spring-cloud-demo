package com.zk.encrypted_transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 请求交互密文传输配置类
 */
@Configuration
@ConditionalOnExpression(value = "${sm2.enable:false}")
public class CipherConfig {

    @Value("${sm2.private-key-a}")
    private String privateKeyA;

    @Value("${sm2.public-key-b}")
    private String publicKeyB;

    // :之后是没有取到配置的默认值，：一定要保留，否则没有配置文件时会报错  这里可以直接改成列表方式配置，不用split
    @Value("#{'${sm2.uri.ignores:}'.empty ? null : '${sm2.uri.ignores:}'.split(',')}")
    private List<String> ignores;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public MyRequestBodyAdvice requestBodyAdvice() {
        return new MyRequestBodyAdvice(privateKeyA, ignores);
    }

    @Bean
    public MyResponseBodyAdvice responseBodyAdvice() {
        return new MyResponseBodyAdvice(publicKeyB, objectMapper, ignores);
    }

    @Bean
    public MyRequestFilter myRequestFilter() {
        return new MyRequestFilter(privateKeyA, ignores);
    }

}
