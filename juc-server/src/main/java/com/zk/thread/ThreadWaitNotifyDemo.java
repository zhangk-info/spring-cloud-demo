package com.zk.thread;

import com.zk.source.Cake;

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
 */
public class ThreadWaitNotifyDemo {

    public static void main(String[] args) {
        Cake cake = new Cake();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "生产者A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "消费者A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "生产者B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    cake.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "消费者B").start();


    }
}
