package com.zk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GateWayApplication {

    //todo 网关需要加入 限流、用户认证、防攻击、日志等安全策略
    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }
}
