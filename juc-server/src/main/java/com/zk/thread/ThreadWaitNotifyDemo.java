package com.zk.thread;

import com.zk.thread.source.Cake;
import lombok.extern.slf4j.Slf4j;

/**
 * 例子：
 * 两个线程 可以操作初始值为0的一个变量，
 * 实现一个线程对变量+1 一个线程对变量-1
 * 实现 交替 来10轮 变量变为初始值0
 * 换种说法:
 * 生产消费模式 没有先生产+1再消费-1
 * 蛋糕房
 * 知识点：
 * wait();notify(); 是Object类的
 * 使用wait(); 必须配置使用while使用 点击wait();进入可看到注释
 *
 * 精确通知顺序访问
 * 生成了蛋糕，只想唤醒消费者线程，精确唤醒
 *
 *
 */
@Slf4j
public class ThreadWaitNotifyDemo {

    public static void main(String[] args) {
        Cake cake = new Cake();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.increment();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, "生产者A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.decrement();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, "消费者A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.increment();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, "生产者B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.decrement();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, "消费者B").start();


    }
}
