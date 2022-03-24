package com.zk.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.configuration.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用redis做的范围查找
 * redis缓存的定时任务，用于处理redis缓存
 * 处理内容：
 * 地理坐标缓存检查（避免中断造成的部分数据未缓存）
 */
@Profile("!location")
@Component
@Slf4j
public class RedisGeoCacheTask {

    private final static Integer pageSize = 50;
    private final static String REDIS_ROOT = "info.zhangk:";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final RedissonClient redissonClient;
    private AtomicInteger page = new AtomicInteger(1);

    public RedisGeoCacheTask(RestTemplate restTemplate, ObjectMapper objectMapper, RedisService redisService, RedissonClient redissonClient) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
        this.redissonClient = redissonClient;
    }

//    /**
//     * 地理坐标缓存检查
//     */
//    @Scheduled(fixedDelay = 7 * 24 * 60 * 60 * 1000)
//    public void checkGeo() {
//        log.debug("RedisGeoCacheTask   -- 开始执行");
//        // 加锁，服务集群情况下只让单一线程进入这里来处理
//        String lockKey = "";
//        RLock lock = redissonClient.getLock(lockKey);
//        lock.lock(3, TimeUnit.SECONDS);
//        try {
//
//            String merchantGeoKey = REDIS_ROOT + ":merchant:address";
//            // 分页获取商户数据
//            while (true) {
//                // 得到所有商户数据
//                Pagination<MerchantListsVo> merchantPage = null;
//                MerchantQuery merchantQuery = new MerchantQuery();
//                try {
//
//                    merchantPage = MerchantSdk.of(restTemplate, objectMapper)
//                            .getMerchants(merchantQuery, page.toString(), pageSize.toString());
//                    List<MerchantListsVo> content = merchantPage.getContent();
//                } catch (Exception e) {
//                    // 服务挂掉的时候进行重试
//                    int i = 0;
//                    // 重试10次
//                    while (i < 10) {
//                        try {
//                            merchantPage = MerchantSdk.of(restTemplate, objectMapper)
//                                    .getMerchants(merchantQuery, page.toString(), pageSize.toString());
//                        } catch (Exception e2) {
//                            log.debug(" RedisGeoCacheTask 获取 第 " + page.get() + " 页数据，重试第 " + (i + 1) + " 次失败。");
//                        }
//                        if (!Objects.isNull(merchantPage)) {
//                            // 得到了跳出循环
//                            break;
//                        } else {
//                            try {
//                                // 先睡3秒再重试
//                                TimeUnit.SECONDS.sleep(3);
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                                break;
//                            }
//                            i++;
//                        }
//                    }
//                }
//
//                if (Objects.isNull(merchantPage)) {
//                    break;
//                }
//
//                List<MerchantListsVo> merchantListVo = merchantPage.getContent();
//                // 如果当前页有数据
//                if (!CollectionUtils.isEmpty(merchantListVo)) {
//                    log.debug(" RedisGeoCacheTask 获取 第 " + page.get() + " 页数据，获取到新的数据，数据条数： " + merchantListVo.size());
//                    merchantListVo.forEach(merchantVo -> {
//                        // 如果没有填坐标 设置坐标为0 0
//                        if (StringUtils.isEmpty(merchantVo.getLongitude()) || StringUtils.isEmpty(merchantVo.getLatitude())) {
//                            redisService.setGeo(merchantGeoKey, merchantVo.getId(), 0d, 0d);
//                        } else {
//                            try {
//                                redisService.setGeo(merchantGeoKey, merchantVo.getId(), Double.parseDouble(merchantVo.getLongitude()), Double.parseDouble(merchantVo.getLatitude()));
//                            } catch (Exception e) {
//                                redisService.setGeo(merchantGeoKey, merchantVo.getId(), 0d, 0d);
//                            }
//                        }
//
//                    });
//                    // 当前页数据条数已经小于每页数据条数了 说明没有下一页的数据了 break
//                    if (merchantListVo.size() < pageSize) {
//                        log.debug(" RedisGeoCacheTask 获取 第 " + page.get() + " 页数据，获取到 " + merchantListVo.size() + " 新的数据，执行完成。");
//                        break;
//                    }
//
//                    page.getAndIncrement();
//
//                } else {
//                    // 当前页已经没有数据了
//                    log.debug(" RedisGeoCacheTask 获取 第 " + page.get() + " 页数据，没有获取到新的数据，执行完成。");
//                    break;
//                }
//
//            }
//            // 重置页码
//            page = new AtomicInteger(1);
//        } finally {
//            if (lock.isLocked()) { // 是否还是锁定状态
//                if (lock.isHeldByCurrentThread()) { // 是当前执行线程的锁
//                    lock.unlock(); // 释放锁
//                }
//            }
//        }
//        log.debug("RedisGeoCacheTask   -- 执行完成");
//    }

    /**
     * 得到半径内的所有商户
     *
     * @param longitude 经度
     * @param latitude  维度
     * @param distance  半径
     * @return
     */
    public List<String> getWithinTheRadius(Double longitude, Double latitude, Double distance) {
        String merchantGeoKey = REDIS_ROOT + ":merchant:address";
        return redisService.geoRadius(merchantGeoKey, longitude, latitude, distance);
    }
}
