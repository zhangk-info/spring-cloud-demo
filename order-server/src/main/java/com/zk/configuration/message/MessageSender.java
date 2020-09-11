package com.zk.configuration.message;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

@EnableBinding(MySource.class) //定义消息的推送管道
public class MessageSender {

    @Resource(name = MySource.channelA) //重要，必须制定bean名称
    private MessageChannel output; // 消息发送管道

    public Boolean send(String json) {
        System.out.println("-----------------------------------发送消息: " + json);
        return output.send(MessageBuilder.withPayload(json).build());
    }
}
 
