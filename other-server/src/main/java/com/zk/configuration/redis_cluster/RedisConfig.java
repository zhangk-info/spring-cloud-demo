package com.zk.configuration.redis_cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * jedis版本需要配置
 * lettuce版本不需要
 * 见 LettuceConnectionConfiguration 和 RedisClusterConfiguration
 */
//@Configuration
//@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisConfig {

    /**
     * Type safe representation of application.properties
     */
    @Autowired
    ClusterConfigurationProperties clusterProperties;

    @Bean
    public RedisConnectionFactory connectionFactory() {

        return new JedisConnectionFactory(
                new RedisClusterConfiguration(clusterProperties.getNodes()));
    }
}
