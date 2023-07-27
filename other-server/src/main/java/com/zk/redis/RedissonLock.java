package com.zk.redis;

import com.zk.commons.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedissonLock {

    @Autowired
    private RedissonClient redissonClient;

    public void saveOrUpdate(Long merchantId) {
        // 对单一商户加锁，同一时间一个商户只能有一条转账在运行
        String lockKey = LockKeys.getKey(merchantId, LockKeys.MERCHANT_PAY);
        RLock lock = redissonClient.getLock(lockKey);

        // 方式1 直接锁并永远等待 waitTime = -1L;
        lock.lock(3, TimeUnit.SECONDS);
        try {
//            BeanUtil.copyProperties(merchantAccountRequest, merchantAccount);
//            merchantAccount = MerchantAccountSdk.of(restTemplate, objectMapper).saveOrUpdate(merchantAccount);
//            // 删除redis缓存数据 在操作完成之后
//            redisService.del(RedisConstant.getKey(MerchantAccountRedisKey.MERCHANT_ACCOUNT.code(), merchantAccount.getMerchantId()));
        } finally {
            if (lock.isLocked()) { // 是否还是锁定状态
                if (lock.isHeldByCurrentThread()) { // 是当前执行线程的锁
                    lock.unlock(); // 释放锁
                }
            }
        }

        // 方式2 尝试锁并处理InterruptedException异常
        try {
            // 尝试加锁，最多等待5秒，上锁以后10秒自动解锁
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                // 业务处理
            } else {
                throw new ServiceException("排队中,请稍后重试!");
            }
        } catch (InterruptedException e) {
            throw new ServiceException("请勿重复操作!");
        } finally {
            if (lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) { // 是当前执行线程的锁
                    lock.unlock(); // 释放锁
                }
            }
        }

        // 方式3 尝试锁并向上层抛出InterruptedException异常
        try {
            // 尝试加锁，最多等待5秒，上锁以后10秒自动解锁
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {
                    // 处理
                    log.info("tryLock thread---{}, lock:{}", Thread.currentThread().getId(), lock);
                } catch (Exception e) {
                } finally {
                    // 解锁 这里不用判断锁状态和是否当前执行线程了，一定是
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            // 处理
            // 保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应
            Thread.currentThread().interrupt();
        }
    }
}
