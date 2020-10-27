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

    /*

        REQUIRED, 支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。
        REQUIRES_NEW, 新建事务，如果当前存在事务，把当前事务挂起。
        SUPPORTS, 支持当前事务，如果当前没有事务，就以非事务方式执行。
        NOT_SUPPORTED, 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
        NEVER, 以非事务方式执行，如果当前存在事务，则抛出异常。
        MANDATORY; 支持当前事务，如果当前没有事务，就抛出异常。


        REQUIRED, 如果有就加入，如果没有就新建；同时提交/回滚。
        REQUIRES_NEW, 有没有都新建；各自提交/回滚。
        SUPPORTS, 有就用；没有就不用。
        NOT_SUPPORTED, 不提供事务；有也不用。
        NEVER, 必须在没有中使用；有就报错。
        MANDATORY; 必须在有中使用；没有报错。


        Read uncommitted(读未提交)
        Read committed(读提交)
        Repeatable read(可重复读取)
        Serializable(可序化)

        Read uncommitted
        Read committed 脏读
        Repeatable read 不可重复读
        Serializable 幻读


     */


}
