package com.zk.redis;

/**
 * 分布式锁枚举资源类
 */
public enum LockKeys {
    /**
     * 卡使用中：订单创建、订单核销、主动退款、超时结清、锁定退款、改变状态
     */
    CARD_IN_USE,
    /**
     * 商户账户操作中
     */
    MERCHANT_PAY,
    ;

    /**
     * 得到key
     *
     * @param source   被锁的资源
     * @param lockKeys 加锁的事件
     * @return 锁key
     */
    public static String getKey(Long source, LockKeys lockKeys) {
        return getKey(source.toString(), lockKeys);
    }

    /**
     * 得到key
     *
     * @param source   被锁的资源
     * @param lockKeys 加锁的事件
     * @return 锁key
     */
    public static String getKey(String source, LockKeys lockKeys) {
        return source + "-" + lockKeys.name();
    }

}
