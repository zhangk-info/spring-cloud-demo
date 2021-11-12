package com.zk.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description： Elasticsearch 配置文件
 * <p>
 * SpringBoot 集成 ES 的步骤：
 * 1、导入依赖
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-getting-started-maven.html
 * 2、编写 ES 配置，给容器中注入一个 RestHighLevelClient，用来操作 9200 端口
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-getting-started-initialization.html
 * 3、参照官方API
 *
 * 改：使用spring-boot-starter-data-elasticsearch 9300端口
 */
@Configuration
public class ElasticsearchConfig {
    @Value("${elasticsearch.ip}")
    private String elasticsearchIp;

    @Bean
    public RestHighLevelClient esRestHighLevelClient() {
        RestClient restClient = RestClient.builder(
                // 这里可以配置多个 es服务，我当前服务不是集群，所以目前只配置一个
                new HttpHost(elasticsearchIp, 9200, "http")).build();

        RestHighLevelClient client = new RestHighLevelClient(restClient);

        return client;
    }

}