package com.zk.thread.source;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

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

		lock.lock();
		//lock 使用模板参考官方 或点击Lock接口看注释
		try {
			// manipulate protected state
			if (number > 0) {
				log.error("-- " + Thread.currentThread().getName() + "\t卖出第" + (number--) + "张票，还剩下" + number + "张");
			}
		} finally {
			lock.unlock();
		}


//		String lockKey = "";
//		RLock lock = redissonClient.getLock(lockKey);
//		lock.lock(3, TimeUnit.SECONDS);
//		try {
//
//		} finally {
//		    if (lock.isLocked()) { // 是否还是锁定状态
//		        if (lock.isHeldByCurrentThread()) { // 是当前执行线程的锁
//		            lock.unlock(); // 释放锁
//		        }
//		    }
//		}

	}

}
