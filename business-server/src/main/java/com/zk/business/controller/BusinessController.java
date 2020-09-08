package com.zk.business.controller;

import com.zk.business.service.BusinessService;
import com.zk.order.OrderTbl;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("business")
@Api("商品采购")
public class BusinessController {
    @Resource
    private BusinessService businessService;

    @PostMapping
    @ApiOperation(value = "采购", tags = "add")
    @GlobalTransactional /*启用全局事务*/
    OrderTbl purchase(String userId, String commodityCode, int orderCount) {

        return businessService.purchase(userId, commodityCode, orderCount);
    }
}
