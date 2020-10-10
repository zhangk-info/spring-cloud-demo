package com.zk.demo;

import java.util.concurrent.*;

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
		// 6 ThreadFactory 线程工厂
		// 7 RejectedExecutionHandler 拒绝策略
		int corePoolSize = 2;
		int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;//7
		int queueSize = Runtime.getRuntime().availableProcessors() + 1 - 2;
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 10L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(queueSize), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());

		//13个顾客请求
		try {
			// 最大 7+5 12个线程提交 超过拒绝
			//new ThreadPoolExecutor.AbortPolicy() 超过(maximumPoolSize + queueSize)报错拒绝
			//new ThreadPoolExecutor.CallerRunsPolicy() 超过(maximumPoolSize + queueSize + 1)返回给主线程调用，谁调我我退谁
			//new ThreadPoolExecutor.DiscardOldestPolicy() 超过(maximumPoolSize + queueSize + 1)放弃调用
			for (int i = 1; i <= 13; i++) {
				int finalI = i;
				threadPoolExecutor.execute(() -> {
					System.out.println(Thread.currentThread().getName() + "\t 办理业务" + finalI);
//					try {
//						TimeUnit.MILLISECONDS.sleep(1l);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					System.out.println(Thread.currentThread().getName() + "\t 办理业务成功" + finalI);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPoolExecutor.shutdown();
		}

	}
}
