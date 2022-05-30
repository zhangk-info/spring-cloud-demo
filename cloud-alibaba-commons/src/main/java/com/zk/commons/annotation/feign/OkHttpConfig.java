//package com.zk.commons.annotation.feign;
//
//import okhttp3.OkHttpClient;
//import org.springframework.cloud.commons.httpclient.DefaultOkHttpClientFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * okHttp配置
// */
//@Configuration
//public class OkHttpConfig {
//
//    /**
//     * 配置okHttp跳过ssl验证
//     * @param okHttpClient
//     * @return
//     */
//    @Bean
//    public feign.okhttp.OkHttpClient client(OkHttpClient okHttpClient) {
//        OkHttpClient.Builder builder = okHttpClient.newBuilder();
//        okhttp3.OkHttpClient httpClient = new DefaultOkHttpClientFactory(builder).createBuilder(true).build();
//        return new feign.okhttp.OkHttpClient(httpClient);
//    }
//
//}
