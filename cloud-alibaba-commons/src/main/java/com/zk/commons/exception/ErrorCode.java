package com.zk.commons.exception;

public enum ErrorCode {
    //权限相关
    NO_DATA_ACCESS_POWER(1000, "无数据访问权限"),
    NO_API_ACCESS_POWER(1001, "无接口访问权限"),
    //数据相关
    DATA_NOT_EXIST(100, "%s数据已清除"),
    DATA_EXIST(101, "数据已存在"),
    SERVER_ERROR(112, "服务器错误"),
    NO_LOGIN(113, "登录信息过期或者未登录"),
    PARAMS_ERR(700, "%s参数错误"),
    //账户
    ACCOUNT_MONEY_IS_NOT_ENOUGH(2000, "账户储值不够支付订单"),
    //订单
    ORDER_SERVICE_TIMEOUT(3000, "当前订单服务繁忙，请稍后重试"),
    //仓储
    STORAGE_IS_NOT_ENOUGH(4000, "当前物品仓储不足");
    public Integer code;
    public String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
