package com.zk.configuration.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 静态资源配置
 *
 * @author zhangkun
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 不一定需要
     * httpMessageConverters(ObjectMapper objectMapper)
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                // 使用自定义的
                jsonMessageConverter.setObjectMapper(objectMapper);
            }
        }
    }
}