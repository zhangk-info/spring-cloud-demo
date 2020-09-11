package com.zk.order.controller;

import com.zk.configuration.message.MessageSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("message")
public class MessageTestController {
    @Resource
    private MessageSender messageSender;

    @GetMapping("send")
    public void send() {
        messageSender.send("test");
    }

}
