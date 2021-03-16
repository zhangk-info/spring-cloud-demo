package com.zk.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolDemo {
    public static void main(String[] args) {

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
        int corePoolSize = 2;
        int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;//7
        int queueSize = Runtime.getRuntime().availableProcessors() + 1 - 2;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(queueSize), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

        //13个顾客请求
        try {
            // 最大 7+5 12个线程提交 默认AbortPolicy超过拒绝并报错
            //new ThreadPoolExecutor.AbortPolicy() 超过(maximumPoolSize + queueSize)放弃调用，报错
            //new ThreadPoolExecutor.CallerRunsPolicy() 超过(maximumPoolSize + queueSize)返回给主线程调用，谁调我我退谁
            //new ThreadPoolExecutor.DiscardOldestPolicy() 超过(maximumPoolSize + queueSize)放弃队列中等待最久的任务，加入当前到队列
            //new ThreadPoolExecutor.DiscardPolicy() 超过(maximumPoolSize + queueSize)放弃调用，也不报错
            for (int i = 1; i <= 30; i++) {

                // 如果总线程数 - 执行中的线程数 >= 队列大小 即 队列满了！ 就 等会儿再插


                Long waitCount = threadPoolExecutor.getTaskCount() - threadPoolExecutor.getCompletedTaskCount() - threadPoolExecutor.getActiveCount();
                System.out.println(waitCount + "=" + threadPoolExecutor.getTaskCount() + "-" + threadPoolExecutor.getCompletedTaskCount() + "-" + threadPoolExecutor.getActiveCount());
                while (waitCount >= queueSize) {
                    System.out.println("wait -- " + i);
                    // 等0.5秒重新获取队列等待数量
                    TimeUnit.MILLISECONDS.sleep(500L);
                    waitCount = threadPoolExecutor.getTaskCount() - threadPoolExecutor.getCompletedTaskCount() - threadPoolExecutor.getActiveCount();
                }

                int finalI = i;
                threadPoolExecutor.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "\t 办理业务" + finalI);
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000L);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    System.out.println(Thread.currentThread().getName() + "\t 办理业务成功" + finalI);
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            threadPoolExecutor.shutdown();
        }

    }
}
