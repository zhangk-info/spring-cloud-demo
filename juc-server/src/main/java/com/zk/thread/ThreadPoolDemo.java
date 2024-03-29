package com.zk.thread;

import com.zk.thread.pool.SimpleRunnable;
import com.zk.thread.pool.SimpleThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadPoolDemo {

    // 用一个原子类来判断是否所有线程都执行完成
    public AtomicInteger runnableNum = new AtomicInteger(0);

    public static void main(String[] args) {
        ThreadPoolDemo demo = new ThreadPoolDemo();
        demo.test();

    }

    public void test() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5); // 一个银行网点，5个受理业务的窗口
//		ExecutorService threadPool = Executors.newSingleThreadExecutor(); // 一个银行网点，1个受理业务的窗口
//		ExecutorService threadPool = Executors.newCachedThreadPool(); // 一个银行网点，可扩展受理业务的窗口

//		以上3个会导致OOM所以都不能使用，来自阿里巴巴Java开发手册
//		说明：Executors 返回的线程池对象的弊端如下：
//		1）FixedThreadPool 和 SingleThreadPool:
//		允许的请求队列长度为 Integer.MAX_VALUE，可能会堆积大量的请求，从而导致 OOM。
//		2）CachedThreadPool 和 ScheduledThreadPool:
//		允许的创建线程数量为 Integer.MAX_VALUE，可能会创建大量的线程，从而导致 OOM。

        // ThreadPoolExecutor的7个参数:
        // 1 corePoolSize 核心线程数
        // 2 maximumPoolSize 最大线程数 //cpu密集型：设置最大线程数为电脑内核数 +1 //io密集型：电脑内核数/阻塞系数
        // 3、4 keepAliveTime 空闲线程存活时间
        // 5 BlockingQueue<Runnable> 任务队列，等待中的任务（提交了尚未执行的）
        // 6 ThreadFactory 线程工厂 //重写线程工厂 目的是调优的时候需要使用线程名字
        // 7 RejectedExecutionHandler 拒绝策略

        int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;//7
        int maximumPoolSize = corePoolSize; // 线上设置核心线程数和最大线程数一致；
        int queueSize = Runtime.getRuntime().availableProcessors() + 1 - 2;
        // 阿里巴巴Java开发手册规定线程工程必须自定义 工厂生产的线程必须使用有意义的线程名字
        ThreadFactory threadFactory = new SimpleThreadFactory();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(queueSize), threadFactory /*Executors.defaultThreadFactory()*/, new ThreadPoolExecutor.DiscardPolicy());

        try {
            // 默认AbortPolicy超过拒绝并报错
            //new ThreadPoolExecutor.AbortPolicy() 超过(maximumPoolSize + queueSize)放弃调用，报错
            //new ThreadPoolExecutor.CallerRunsPolicy() 超过(maximumPoolSize + queueSize)返回给主线程调用，谁调我我退谁
            //new ThreadPoolExecutor.DiscardOldestPolicy() 超过(maximumPoolSize + queueSize)放弃队列中等待最久的任务，加入当前到队列
            //new ThreadPoolExecutor.DiscardPolicy() 超过(maximumPoolSize + queueSize)放弃调用，也不报错
            for (int i = 1; i <= 100; i++) {

                // 如果 总任务数量-已完成任务数量-执行中的线程数 >= 队列大小 即 队列满了！ 就 等会儿再加
                long waitCount = threadPoolExecutor.getTaskCount() - threadPoolExecutor.getCompletedTaskCount() - threadPoolExecutor.getActiveCount();
                log.debug("队列等待数量=总任务数量-已完成任务数量-执行中的线程数：" + waitCount + "=" + threadPoolExecutor.getTaskCount() + "-" + threadPoolExecutor.getCompletedTaskCount() + "-" + threadPoolExecutor.getActiveCount());
                while (waitCount >= queueSize) {
                    log.debug("队列等待数量超过，开始等待 wait -- 当前队列等待数量：" + waitCount + " & 当前加入第" + i + "个任务");
                    // 等0.5秒重新获取队列等待数量
                    TimeUnit.MILLISECONDS.sleep(500L);
                    waitCount = threadPoolExecutor.getTaskCount() - threadPoolExecutor.getCompletedTaskCount() - threadPoolExecutor.getActiveCount();
                }

                // 自定义Runnable进行任务办理
                Runnable runnable = new SimpleRunnable(i, this);
                // 增加
                runnableNum.incrementAndGet();
                // 核心方法，加入任务
                threadPoolExecutor.execute(runnable);

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            while (runnableNum.get() > 0) {
                log.error(runnableNum.get() + "-- 遍历等待线程池处理结束  等 1秒钟 重新获取 池活线程数量");
                try {
                    // 等 1秒钟 重新获取 池活线程数量
                    TimeUnit.MILLISECONDS.sleep(1000L);
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }

            threadPoolExecutor.shutdown();
        }

    }
}
