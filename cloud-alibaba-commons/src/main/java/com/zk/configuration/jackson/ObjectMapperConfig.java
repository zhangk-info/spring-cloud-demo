package com.zk.configuration.jackson;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@Configuration
public class ObjectMapperConfig {


    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";


    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 下面配置解决LocalDateTime序列化的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 设置时区 保持一致 重要
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 排序key
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        //忽略空bean转json错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略在json字符串中存在，在java类中不存在字段，防止错误。
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列换成json时,将所有的long变成string  因为js中得数字类型不能包含所有的java long值
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //日期序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        //日期反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }


    /**
     * json序列化配置
     *
     * @param objectMapper objectMapper
     * @return HttpMessageConverters
     */
    @Bean
    public HttpMessageConverters httpMessageConverters(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        return new HttpMessageConverters(jackson2HttpMessageConverter);
    }
}
