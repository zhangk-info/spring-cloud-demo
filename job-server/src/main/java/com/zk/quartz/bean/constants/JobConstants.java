package com.zk.quartz.bean.constants;

/**
 * 
 * @date 2020/12/10 8:48
 * @description:
 */
public interface JobConstants {

    /**
     * 消息配置Service
     */
    String NOTIFY_CONFIG_SERVICE = "sysNotifyConfigServiceImpl";
    String NOTIFY_CONFIG_SERVICE_METHOD_NAME = "sendMsgTriggerByTime";

    /**
     * 错失执行策略（1错失周期立即执行 2错失周期执行一次 3下周期执行）
     */
    String misfire_Policy_NOW = "1";
    String misfire_Policy_NOE = "2";
    String misfire_Policy_NEXT = "3";

    /**
     * 重复类型，0.不重复 1.天 2.周 3.月 4.年
     */
    String REPEAT_RATE_UNIT_NON = "0";
    String REPEAT_RATE_UNIT_DAY = "1";
    String REPEAT_RATE_UNIT_WEEK = "2";
    String REPEAT_RATE_UNIT_MONTH = "3";
    String REPEAT_RATE_UNIT_YEAR = "4";
}
