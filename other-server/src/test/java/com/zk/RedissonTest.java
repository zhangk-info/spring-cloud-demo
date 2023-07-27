package com.zk;

import cn.hutool.core.date.DateUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = UtilApplication.class)
public class RedissonTest {

    @Autowired
    private Redisson redisson;

    @Test
    public void testLock() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 30, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder()
                .setNameFormat("redis-lock-test-consumer-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executor.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    int finalJ = j;
                    executor.execute(() -> {
                        lockOneThread(finalI, finalJ);
                    });
                }
            });
        }
        Thread.sleep(10000);
        while (executor.getActiveCount() > 1) {
            System.out.println(executor.getActiveCount());
            Thread.sleep(1000);
        }
    }

    public void lockOneThread(int i, int j) {
        String lockKey = "lockKey";
        RLock lock = redisson.getLock(lockKey);
        long before = new Date().getTime();
        try {
            // 尝试加锁，最多等待3秒，上锁以后如果没有锁续费3秒自动解锁
            if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                log.info("得到锁的线程: {},i {},j {},{}, 等待时间: {}", Thread.currentThread().getId(), i, j, DateUtil.now(), new Date().getTime() - before);
                Thread.sleep(4000);
            } else {
                log.info("尝试获取锁失败的线程: {},i {},j {},{}, 等待时间: {}", Thread.currentThread().getId(), i, j, DateUtil.now(), new Date().getTime() - before);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isLocked()) {
                // 是当前执行线程的锁
                if (lock.isHeldByCurrentThread()) {
                    // 释放锁
                    lock.unlock();
                    log.info("释放锁的线程: {},i {},j {},{},等待时间: {}", Thread.currentThread().getId(), i, j, DateUtil.now(), new Date().getTime() - before);
                }
            }
        }
    }
}
