package com.zk.configuration.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {

    /**
     * 注入AuthenticationManager ，密码模式用到
     */
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private DataSource dataSource;

    /**
     * 对Jwt认证解码时，增加一个公钥
     * RSA对称加密
     * JwtAccessTokenConverter：对Jwt来进行编码以及解码的类
     * 可以在这里增加附件信息、自定义签名、AccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        try {
            String publicKey = ResourceUtil.readUtf8Str("publicKey.txt");
            converter.setVerifierKey(publicKey);
        } catch (Exception e) {
            log.error("read publicKey.txt error,please check resource dir has 'publicKey.txt' file ?!", e);
            throw new RuntimeException("没有找到公钥文件，请检查！！！");
        }

        return converter;
    }

    /**
     * 设置token 由Jwt产生，不使用默认的透明令牌 JwtTokenStore
     */
    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * 设置 AuthorizationServerEndpointsConfigurer
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(jwtTokenStore())
                .accessTokenConverter(accessTokenConverter());
    }

    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

}