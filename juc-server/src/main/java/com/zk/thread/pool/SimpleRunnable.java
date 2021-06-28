package com.zk.thread.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 自定义简单Runnable
 */
@Slf4j
public class SimpleRunnable implements Runnable {

    /**
     * 当前执行的任务序号
     */
    private final Integer number;

    /**
     * 构造方法
     *
     * @param number 任务序号
     */
    public SimpleRunnable(Integer number) {
        this.number = number;
    }

    /**
     * 抽象工厂方法
     */
    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(2000L);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        log.debug("线程[" + Thread.currentThread().getName() + "]  -- 办理第" + number + "个业务成功");
    }
}
