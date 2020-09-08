package com.zk.business.service.impl;

import com.zk.business.service.BusinessService;
import com.zk.order.OrderService;
import com.zk.order.OrderTbl;
import com.zk.storage.StorageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Resource
    private StorageService storageService;
    @Resource
    private OrderService orderService;

    /**
     * 采购
     */
    public OrderTbl purchase(String userId, String commodityCode, int orderCount) {

        storageService.deduct(commodityCode, orderCount);

        return orderService.create(userId, commodityCode, orderCount);
    }
}