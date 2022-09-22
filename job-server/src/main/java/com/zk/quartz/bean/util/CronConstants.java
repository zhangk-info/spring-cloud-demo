package com.zk.quartz.bean.util;

import cn.hutool.core.lang.Pair;

/**
 * 
 * @date 2020/12/8 11:37
 * @description:
 */
public interface CronConstants {
    Pair<String, Integer> TIME_UNIT_SECONDS = new Pair<>("SECONDS", 0);
    Pair<String, Integer> TIME_UNIT_MINUTES = new Pair<>("MINUTES", 1);
    Pair<String, Integer> TIME_UNIT_HOURS = new Pair<>("HOURS", 2);
    Pair<String, Integer> TIME_UNIT_DAY_OF_MONTH = new Pair<>("DAY_OF_MONTH", 3);
    Pair<String, Integer> TIME_UNIT_MONTH = new Pair<>("MONTH", 4);
    Pair<String, Integer> TIME_UNIT_DAY_OF_WEEK = new Pair<>("DAY_OF_WEEK", 5);
    Pair<String, Integer> TIME_UNIT_YEAR = new Pair<>("YEAR", 6);

    /**
     * 每1
     */
    Integer TYPE_EVERY = 1;
    /**
     * 间隔
     */
    Integer TYPE_INTERVAL = 2;
    /**
     * 指定时间
     */
    Integer TYPE_AT_TIME = 3;
    /**
     * 周期执行
     */
    Integer TYPE_CYCLE = 4;
    /**
     * ? 号
     */
    Integer TYPE_QUESTION_MARK = 5;
}
