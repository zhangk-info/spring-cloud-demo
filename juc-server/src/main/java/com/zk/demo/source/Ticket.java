package com.zk.demo.source;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 可重入锁
 */
@Slf4j
public class Ticket {

    private int number = 10000;

    private Lock lock = new ReentrantLock();

    //以前使用synchronized 来避免重复操作 现在使用lock
    public void saleTicket() {

        //lock 使用模板参考官方 或点击Lock接口看注释
        if (lock.tryLock()) {
            try {
                // manipulate protected state
                if (number > 0) {
                    log.error("-- " + Thread.currentThread().getName() + "\t卖出第" + (number--) + "张票，还剩下" + number + "张");
                }
            } finally {
                lock.unlock();
            }
        } else {
            // perform alternative actions
            log.error("线程获取锁失败");
        }


    }

}
