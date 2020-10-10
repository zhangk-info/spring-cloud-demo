package com.zk.demo.thread;

import com.zk.demo.source.Ticket;

/**
 * 例子：
 * 三个售票员 卖出30张票
 *
 * 知识点：
 * 1.
 * .run()之后是不是线程立马启动
 * 不是
 * 线程什么时候启动？
 * 不知道
 * 线程的运行是和操作系统有关的。所以。run()只是表示当前线程已经就绪，当操作系统cpu处理这个线程的时候，这个线程才启动了。
 * 2.
 * 线程状态 enum Thread.State:
 * NEW 新建
 * RUNNABLE 就绪
 * BLOCKED 阻塞 （wait , sleep）会造成阻塞 wait:放权，放开锁去睡，醒来重新拿锁; sleep:不放权，拿着锁去睡，醒来锁在手上，其他线程访问资源得等着拿锁；
 * WAITING 等待，一直等。程序不死，线程就等。
 * TIMED_WAITING 过时不候。等一段时间。一段时间没有重新唤醒，线程结束。
 * TERMINATED 终结
 */
public class SaleTicket {

    public static void main(String[] args) throws Exception {
        Ticket t = new Ticket();

        //售票员1
        new Thread(() ->  { for (int i = 0; i < 11000; i++) t.saleTicket(); }, "售票员1").start();

        //售票员2
        new Thread(() -> { for (int i = 0; i < 11000; i++) t.saleTicket(); }, "售票员2").start();

        //售票员3
        new Thread(() ->  { for (int i = 0; i < 11000; i++) t.saleTicket(); }, "售票员3").start();
    }
}
