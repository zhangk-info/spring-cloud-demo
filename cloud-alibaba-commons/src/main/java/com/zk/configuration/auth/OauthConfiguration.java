package com.zk.configuration.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@Slf4j
public class OauthConfiguration {

    @Bean
    public SignatureVerifier signerVerifier() {
        try {
            String publicKey = ResourceUtil.readUtf8Str("publicKey.txt");
//            String publicKey = "-----BEGIN PUBLIC KEY-----" +
//                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkc5r6LGBn0TsWYxXxVAs" +
//                    "PjjsqQSCG+3IRDgw8bOBAXfB46zb6xdxu0KQE83fD/IDs5547JjWs+8DbDHj+JgO" +
//                    "Uss/UjC5XZJWVr5HJ0FAGZ0QeNPEara/66qbxhWixi2th2Vd0dRZC60XSl1OUvwz" +
//                    "Ow1Mx+nErAYxfFgaFJFIin9lNZY9OXCAs1jrmyYvPqsUf0GHWvAlRJr19jhnRjDg" +
//                    "hF50+An/q6Dp+yPjwDqqqXdzkBwrQ8C96Id0pYYWK5aQnRyU4CU+lQZfWtGrtrnz" +
//                    "px9KD99Avxe+p5FQMNYzlnlzsv8nbjXhLkH3OOW+4OIgl2q1m8rRvqsxrjH8fm3w" +
//                    "7QIDAQAB" +
//                    "-----END PUBLIC KEY-----";
            //这里踩坑 BEGIN PUBLIC KEY 必须大写！！！
            return new RsaVerifier(publicKey);
        } catch (Exception e) {
            log.error("read publicKey.txt error,please check resource dir has 'publicKey.txt' file ?!", e);
            throw new RuntimeException("没有找到公钥文件，请检查！！！");
        }
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    //
//    @Bean 不一定要给spring管理 tokenStore给了就行 resources.tokenStore(tokenStore);
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        //用作 JWT 转换器
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        String publicKey = ResourceUtil.readUtf8Str("publicKey.txt");
        converter.setVerifierKey(publicKey); //设置公钥
        return converter;
    }
}
