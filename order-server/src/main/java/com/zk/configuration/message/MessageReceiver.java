package com.zk.configuration.message;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//todo 放开
//@EnableBinding(MySink.class)
public class MessageReceiver {


//    @StreamListener(MySink.channelA)
    public void input(Message<String> message) {
        System.out.println("-----------------------------------接收消息：" + message.getPayload() + "\t");
        message.getPayload();
    }

}