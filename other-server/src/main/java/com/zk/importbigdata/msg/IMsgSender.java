package com.zk.importbigdata.msg;

import com.zk.importbigdata.msg.ImportMsg;

@FunctionalInterface
public interface IMsgSender {
    void send(ImportMsg importMsg);
}