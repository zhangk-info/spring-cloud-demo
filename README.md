## spring-cloud-demo 


#### 本demo使用spring-cloud + spring-cloud-alibaba搭建 三个测试服务数据表来自seata.io的示例
#### nacos使用的是https://nacos.zhangk.info 其它组件详见 https://www.zhangk.info
#### 以下为基本约定和公共配置 
* cloud-alibaba-commons 公共组件基础包 
* cloud-gateway-gateway 网关服务 
* auth-server 认证服务 
* com.zk-commons 服务使用的公共配置包
* admin-server spring-boot-admin-starter-server服务监控 没什么用因为有更好的
#### 以下为服务 
* account-server 账户服务 
* order-server 订单服务 
* storage-server 仓储服务 
* juc-server juc/jvm学习
* es-server elasticsearch
* https://github.com/redisson/redisson redisson分布式锁


#### 测试环境有部分配置或者功能被注释，请检查todo 
#### 服务最好一个一个启动，我服务器1核的扛不住并发


#### 服务监控建议： 
* pinpoint 
* prometheus + grafana + alerting GPE监控系统

#### 日志分析系统建议： 
* graylog 
* elk

### sa-plus sa-plus的微服务版本, 基于Spring-Cloud-Alibaba
http://sa-plus.dev33.cn/
#### sa-plus 微服务版
https://gitee.com/sz6/sp-cloud

