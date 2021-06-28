package com.zk.thread.source;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * synchronized wait notify -> lock await signal
 */
@Slf4j
public class Cake {

    public int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();


    /**
     * 生产
     * @throws InterruptedException
     */
    public synchronized void increment() throws InterruptedException {

        lock.lock();
        try {
            //判断
            while (number != 0) {
                condition.await();
            }
            //干活
            number++;
            log.error(Thread.currentThread().getName() + "\t" + number);
            //通知
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 消费
     * @throws InterruptedException
     */
    public void decrement() throws InterruptedException {

        lock.lock();
        try {
            while (number == 0) {
                condition.await();
            }
            number--;
            log.error(Thread.currentThread().getName() + "\t" + number);
            condition.signalAll();
        } finally {
            lock.unlock();
        }


    }
//
//    public synchronized void increment() throws InterruptedException {
//        //判断
//        while (number != 0) {
//            this.wait();
//        }
//        //干活
//        number++;
//        log.error(Thread.currentThread().getName() + "\t" + number);
//        //通知
//        this.notifyAll();
//    }

//    public synchronized void decrement() throws InterruptedException {
//        while (number == 0) {
//            this.wait();
//        }
//        number--;
//        log.error(Thread.currentThread().getName() + "\t" + number);
//        this.notifyAll();
//    }
}
