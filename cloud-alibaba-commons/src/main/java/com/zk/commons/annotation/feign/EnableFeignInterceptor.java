package com.zk.commons.annotation.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 这个注解的作用是
 * 在openfeign进行服务间调用的时候将请求头中的所有内容（token）同步传递到被调用的服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
@Import({FeignConfig.class})
public @interface EnableFeignInterceptor {
}
