package com.zk.designpattern.facade;


import org.apache.catalina.connector.RequestFacade;

/**
 * 门面模式
 * 为了使子系统更加容易调用，为子系统中的一系列接口，提供一个一致的接口（门面）
 */
public class Facade {

    public static void main(String[] args) {
        BusinessService businessService = new BusinessService();
        businessService.business();

        // 列子 controller -> service controller就是门面
        RequestFacade facade;// HttpServletRequest的门面
    }
}

/**
 * 业务接口 门面
 */
class BusinessService {

    void business() {
        OrderService orderService = new OrderService();
        StorageService storageService = new StorageService();
        AccountService accountService = new AccountService();
        orderService.createOrder();
        storageService.subtractStorage();
        accountService.debit();
    }
}

/**
 * 订单
 */
class OrderService {

    /**
     * 创建订单
     */
    public void createOrder() {

    }
}

/**
 * 仓储
 */
class StorageService {

    /**
     * 减仓储
     */
    public void subtractStorage() {

    }
}

/**
 * 账户
 */
class AccountService {

    /**
     * 扣款
     */
    public void debit() {

    }
}