package com.zk.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.account.AccountService;
import com.zk.order.entity.OrderTbl;
import com.zk.order.mapper.OrderTblMapper;
import com.zk.order.service.IOrderTblService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
@Service
public class OrderTblServiceImpl extends ServiceImpl<OrderTblMapper, OrderTbl> implements IOrderTblService {

    private AccountService accountService;

    public OrderTbl create(String userId, String commodityCode, int orderCount) {

        int orderMoney = calculate(commodityCode, orderCount);

        accountService.debit(userId, orderMoney);

        OrderTbl order = new OrderTbl();
        order.userId = userId;
        order.commodityCode = commodityCode;
        order.count = orderCount;
        order.money = orderMoney;

        // INSERT INTO orders ...
        this.baseMapper.insert(order);
        return order;
    }

    private int calculate(String commodityCode, int orderCount) {
        return 1 * orderCount;
    }
}
