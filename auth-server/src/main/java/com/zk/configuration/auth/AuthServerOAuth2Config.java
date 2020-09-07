package com.zk.configuration.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import com.zk.configuration.auth.exception.LoginWebResponseExceptionTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
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
    @Resource
    private LoginWebResponseExceptionTranslator loginWebResponseExceptionTranslator;
    @Resource
    private SecurityUserDetailService securityUserDetailService;

    /**
     * 对Jwt签名时，增加一个密钥
     * RSA对称加密
     * JwtAccessTokenConverter：对Jwt来进行编码以及解码的类
     * 可以在这里增加附件信息、自定义签名、AccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

//
//        进入[%JAVA_HOME%]路径下
//        生成JKS Java KeyStore文件
//        keytool -genkeypair -alias mytest -keyalg RSA  -keypass mypass -keystore mytest.jks -storepass mypass
//        导出公钥 由于windows导出不了公钥，所以找了个util ExportCert
//        不可用 ： windows : keytool -list -rfc --keystore mytest.jks
//        linux : openssl x509 -inform pem -pubkey

//        //.jks文件方式 setKeyPair 然后从文件中通过password和alias得到KeyPair
//        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("private.key"), "zhangk..123".toCharArray());
//        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("zhangk"));

        //privateKey方式 setSigningKey
        try {
            String privateKey = ResourceUtil.readUtf8Str("privateKey.txt");
            converter.setSigningKey(privateKey);
            //这里踩坑，SigningKey 和 VerifierKey 不能在同一个项目中同时被设置
//            String publicKey = ResourceUtil.readUtf8Str("publicKey.txt");
//            converter.setVerifierKey(publicKey);
        } catch (Exception e) {
            log.error("read privateKey.txt error,please check resource dir has 'privateKey.txt' file ?!", e);
            throw new RuntimeException("没有找到私钥文件，请检查！！！");
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
                .exceptionTranslator(loginWebResponseExceptionTranslator)
                .authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailService)
                .tokenStore(jwtTokenStore())
                .accessTokenConverter(accessTokenConverter());
    }

    /**
     * 设置 ClientDetailsServiceConfigurer
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //这里设置的是密码模式、授权码模式请求时候用到的请求头 生成之后为
                // 踩坑 这里的密码要过passwordEncoder 所以要sm2加密
                // 或者自己在 SM4PasswordEncoder的matches去处理 当然 不建议，容易被其他用户登录的偷懒而不使用sm2加密密码传输
                // Basic emhhbmdrOjA0QjA1NzUxRDk1MTM1N0Q5RUM1NjlEOEVBOEJFNEVFNDY4N0MzRTFERjQyQUVFQUU0OUE3QTQxRUU5MzAyNzkzNDVFMkYzQUVFRkY3MzUyNTQ0Q0RBMTRFM0M3Q0QwMkRGMjUxNzlENDVCM0QxMkI5NDQ0RkFGODIwNTdERDlBN0YzQUI2NUJBNDE0NUMyMjFGMDI5RDhDNkQ3NjRGNDBFQjhBNkQ1M0VEODQ1QUE0Njg3OTAxQ0E5NjAyOTRFNjA1RENERTA5REFFMzJERTk0QTI2OTAxMg==
                // 得到的token是：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDAwNzE3ODAsInVzZXJfbmFtZSI6InpoYW5nayIsImp0aSI6IjZmYjYyMTkwLWI1NTYtNDE3MC05ZmEwLTM4OTRmZGIwNmQ3YiIsImNsaWVudF9pZCI6InpoYW5nayIsInNjb3BlIjpbImFsbCJdfQ.s2VVuumoQeGsAFJPBhkh8w7UrvdS3TPdGy_DpCN2FqI
                .withClient("zhangk")
                .secret("zhangk..123")
                .scopes("all")
                //设置支持[密码模式、授权码模式、token刷新]
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                //设置两个token的过期时间
                .accessTokenValiditySeconds(60 * 60 * 24 * 7)
                .refreshTokenValiditySeconds(60 * 60 * 24 * 8);

//        //这里是隐式授权模式的示例
//        clients.inMemory()
//                .withClient("clientapp")
//                .secret("112233")
//                .accessTokenValiditySeconds(60)
//                .redirectUris("http://localhost:9001/callback")
//                .authorizedGrantTypes("implicit")
//                .scopes("admin", "visitor");
    }

    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

}