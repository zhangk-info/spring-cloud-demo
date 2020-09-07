## spring-cloud-demo 


#### 本demo使用spring-cloud + spring-cloud-alibaba搭建 三个测试服务数据表来自seata.io的示例
#### nacos使用的是https://nacos.zhangk.info 其它组件详见 https://www.zhangk.info
#### 以下为基本约定和公共配置 
* cloud-alibaba-commons 公共组件基础包 
* cloud-gateway-gateway 网关服务 
* auth-server 认证服务 
* zk-commons 服务使用的公共配置包
#### 以下为服务 
* account-server 账户服务 
* order-server 订单服务 
* storage-server 仓储服务 


#### 测试环境有部分配置或者功能被注释，请检查todo 