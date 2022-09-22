package com.zk.order.service;

import com.zk.order.entity.OrderTbl;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
public interface IOrderTblService extends IService<OrderTbl> {

    /**
     * 创建订单
     */
    OrderTbl create(String userId, String commodityCode, int orderCount);
}
