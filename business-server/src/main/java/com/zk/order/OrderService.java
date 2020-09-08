package com.zk.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "order-server",
        contextId = "OrderService",
        path = "order/"
)
public interface OrderService {

    @PostMapping("order-tbl/create")
    OrderTbl create(@RequestParam(name = "userId") String userId, @RequestParam("commodityCode") String commodityCode, @RequestParam("orderCount") int orderCount);
}
