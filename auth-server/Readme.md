## auth-server


#### 本server使用spring-cloud + spring-cloud-starter-oauth2 搭建
#### spring-cloud-starter-oauth2 = spring-security-oauth2 + spring-security-oauth2-autoconfigure
```
<dependency>
    <groupId>com.sgitg</groupId>
    <artifactId>sgcc-sm</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### 这个包可以替换为使用hutool的国密smutil，需要重写passwordEncoder就可以了
#### 获取token方式
```
post localhost:90/oauth/token 
或者过网关
post localhost:9527/auth/oauth/token

请求头：
Basic emhhbmdrOjA0QjA1NzUxRDk1MTM1N0Q5RUM1NjlEOEVBOEJFNEVFNDY4N0MzRTFERjQyQUVFQUU0OUE3QTQxRUU5MzAyNzkzNDVFMkYzQUVFRkY3MzUyNTQ0Q0RBMTRFM0M3Q0QwMkRGMjUxNzlENDVCM0QxMkI5NDQ0RkFGODIwNTdERDlBN0YzQUI2NUJBNDE0NUMyMjFGMDI5RDhDNkQ3NjRGNDBFQjhBNkQ1M0VEODQ1QUE0Njg3OTAxQ0E5NjAyOTRFNjA1RENERTA5REFFMzJERTk0QTI2OTAxMg==
请求参数：
grant_type:password
username:zhangk
password:04B05751D951357D9EC569D8EA8BE4EE4687C3E1DF42AEEAE49A7A41EE930279345E2F3AEEFF7352544CDA14E3C7CD02DF25179D45B3D12B9444FAF82057DD9A7F3AB65BA4145C221F029D8C6D764F40EB8A6D53ED845AA4687901CA960294E605DCDE09DAE32DE94A269012


请求头为
clients.inMemory()
.withClient("zhangk")
.secret("zhangk..123")
设置的帐号密码生成的basic认证请求头

grant_type 为oauth2的password模式
password 为国密sm4加密的zhangk..123

```

#### 必须了解类：
```
AuthServerOAuth2Config oauth2配置类
SecurityUserDetailService 必须实现UserDetailsService的loadUserByUsername方法
JwtInfoConvert jwt生成token时增加附件树形
SecurityConfig 继承WebSecurityConfigurerAdapter并重写相关配置：SecurityUserDetailService，passwordEncoder
ServerResourceConfig 设置需要拦截的请求路径等配置信息
SM4PasswordEncoder 密码认证
```

#### 非必须
```
ImageCodeValidateFilter oauth/token请求同时加入验证码拦截
```