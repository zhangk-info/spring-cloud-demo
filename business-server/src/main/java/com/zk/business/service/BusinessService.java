package com.zk.business.service;

import com.zk.order.OrderTbl;

public interface BusinessService {

    /**
     * 采购
     *
     * @param userId
     * @param commodityCode
     * @param orderCount
     */
    OrderTbl purchase(String userId, String commodityCode, int orderCount);
}
