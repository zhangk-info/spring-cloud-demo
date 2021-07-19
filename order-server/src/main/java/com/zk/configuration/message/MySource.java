package com.zk.configuration.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 *  stream:
 *    bindings: # 服务的整合处理
 *      channelA: # 这个名字是一个通道的名称 output的send 和 input的receive都是用这个通道
 *      destination: testExchange # 表示要使用的Exchange名称定义 绑定交换机的名称
 */
public interface MySource {
    String channelA = "channelA";

    @Output(MySource.channelA)
    MessageChannel channelA();
}