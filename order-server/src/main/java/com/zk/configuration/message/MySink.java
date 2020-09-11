package com.zk.configuration.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MySink {
    String channelA = "channelA";

    @Input(MySink.channelA)
    SubscribableChannel channelA();
}
