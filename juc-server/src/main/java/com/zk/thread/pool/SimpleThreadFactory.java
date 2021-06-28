package com.zk.thread.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义简单线程工厂
 */
public class SimpleThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    /**
     * 构造方法
     */
    public SimpleThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        // 线程的组
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        // 线程的名字 这里可以加上本机地址什么的,便于故障检查
        namePrefix = "自定义简单线程池：simple-pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    /**
     * 工厂方法
     *
     * @param target
     * @return
     */
    public Thread newThread(Runnable target) {
        Thread t = new Thread(group, target,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        // 不是守护线程
        if (t.isDaemon())
            t.setDaemon(false);
        // 设置优先级为默认的5
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}