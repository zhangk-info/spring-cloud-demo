package com.zk.quartz.bean.util;

import cn.hutool.core.lang.Pair;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @date 2020/12/8 11:43
 * @description:
 */
@Getter
@Setter
public class ScheduleModel {
    /**
     * 时间单位 yyyy-MM-dd HH:mm:ss
     */
    private Pair<String, Integer> TimeUnit;
    /**
     * 类型 1.每1TimeUnit 2.每隔interval执行，从start开始 3.具体atTime 4.周期从from到to
     */
    private int type;
    /**
     * 间隔时间
     */
    private int interval;
    /**
     * 从多少开始
     */
    private int start;
    /**
     * 具体时间，以“,”隔开
     */
    private String atTime;

    /**
     * 周期-从
     */
    private int from;
    /**
     * 周期-至
     */
    private int to;

    public ScheduleModel(Pair<String, Integer> timeUnit, int type, int interval, int start,
                         String atTime, int from, int to) {
        TimeUnit = timeUnit;
        this.type = type;
        this.interval = interval;
        this.start = start;
        this.atTime = atTime;
        this.from = from;
        this.to = to;
    }

    public ScheduleModel(Pair<String, Integer> timeUnit, int type, int interval, int start) {
        TimeUnit = timeUnit;
        this.type = type;
        this.interval = interval;
        this.start = start;
    }

    public ScheduleModel(Pair<String, Integer> timeUnit, int type, String atTime) {
        TimeUnit = timeUnit;
        this.type = type;
        this.atTime = atTime;
    }

    public ScheduleModel(int from, int to, int type, Pair<String, Integer> timeUnit) {
        TimeUnit = timeUnit;
        this.type = type;
        this.from = from;
        this.to = to;
    }

    public ScheduleModel(Pair<String, Integer> timeUnit, int type) {
        TimeUnit = timeUnit;
        this.type = type;
    }
}
