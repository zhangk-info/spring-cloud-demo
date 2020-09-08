package com.zk.order.controller;


import com.zk.commons.exception.ErrorCode;
import com.zk.commons.exception.ServiceException;
import com.zk.order.entity.OrderTbl;
import com.zk.order.service.IOrderTblService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
@RestController
@RequestMapping("/order/order-tbl")
public class OrderTblController {
    @Resource
    private IOrderTblService orderTblService;

    @PostMapping("create")
    OrderTbl create(String userId, String commodityCode, int orderCount) {
        if (!userId.equals("1") || !commodityCode.equals("1") || orderCount < 0) {
            throw new ServiceException(ErrorCode.PARAMS_ERR, "用户、货物编号或者货物数量");
        }
        return orderTblService.create(userId, commodityCode, orderCount);
    }
}
