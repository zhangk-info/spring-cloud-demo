package com.zk.configuration.message;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

@EnableBinding(MySink.class)
public class MessageReceiver {


    @StreamListener(MySink.channelA)
    public void input(Message<String> message) {
        System.out.println("-----------------------------------接收消息：" + message.getPayload() + "\t");
        message.getPayload();
    }

}