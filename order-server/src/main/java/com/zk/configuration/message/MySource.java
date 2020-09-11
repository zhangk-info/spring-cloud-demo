package com.zk.configuration.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MySource {
    String channelA = "channelA";

    @Output(MySource.channelA)
    MessageChannel channelA();
}