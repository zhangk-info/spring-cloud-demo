package com.zk.quartz.bean.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import com.zk.quartz.bean.constants.JobConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @date 2020/12/8 9:43
 * @description:
 */
public class CronUtil {

    public static String createCronNoRepeat(Date scheduleTime) {
        return createCron(JobConstants.REPEAT_RATE_UNIT_NON, scheduleTime);
    }

    public static String createCron(String repeatRateUnit, Date scheduleTime) {
        return createCron(repeatRateUnit, scheduleTime, null, null);
    }

    /**
     * @param repeatRateUnit 重复频率单位
     * @param scheduleTime   时间
     * @param repeatRate     每几
     * @param repeatWeek     具体星期几
     * @return
     */
    public static String createCron(String repeatRateUnit, Date scheduleTime, Integer repeatRate, String repeatWeek) {
        Set<ScheduleModel> scheduleModels = new HashSet<>();
        int second = DateUtil.second(scheduleTime);
        int minute = DateUtil.minute(scheduleTime);
        int hour = DateUtil.hour(scheduleTime, true);
        int dayOfMonth = DateUtil.dayOfMonth(scheduleTime);
        int month = DateUtil.month(scheduleTime) + 1;
        int dayOfWeek = DateUtil.dayOfWeek(scheduleTime);
        int year = DateUtil.year(scheduleTime);

        // 秒
        scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_SECONDS, CronConstants.TYPE_AT_TIME, String.valueOf(second)));
        // 分
        scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_MINUTES, CronConstants.TYPE_AT_TIME, String.valueOf(minute)));
        // 时
        scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_HOURS, CronConstants.TYPE_AT_TIME, String.valueOf(hour)));
        boolean repeatRateNotNull = repeatRate != null;
        if (repeatRateUnit.equals("0")) {
            // 不重复
            scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_MONTH, CronConstants.TYPE_AT_TIME, String.valueOf(dayOfMonth)));
            scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_MONTH, CronConstants.TYPE_AT_TIME, String.valueOf(month)));
            scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_YEAR, CronConstants.TYPE_AT_TIME, String.valueOf(year)));
        } else if (repeatRateUnit.equals("1")) {
            /**
             * 天
             * （1）repeatRate不为空，表示每repeatRate天执行
             * （2）repeatRate为空，表示每天执行
             */
            if (repeatRateNotNull) {
                scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_MONTH, CronConstants.TYPE_INTERVAL, repeatRate, dayOfMonth));
            }
        } else if (repeatRateUnit.equals("2")) {
            /**
             * 周
             * （1）repeatRate不为空或者repeatWeek为空，表示每repeatRate周执行
             */
            if (repeatRateNotNull) {
                scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_WEEK, CronConstants.TYPE_INTERVAL, repeatRate, dayOfWeek));
            } else {
                scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_WEEK, CronConstants.TYPE_AT_TIME, repeatWeek == null ? String.valueOf(dayOfWeek) : repeatWeek));
            }
            // 月份设置为"?"
            scheduleModels.add(new ScheduleModel(dayOfMonth, dayOfMonth, CronConstants.TYPE_QUESTION_MARK, CronConstants.TIME_UNIT_DAY_OF_MONTH));
        } else if (repeatRateUnit.equals("3")) {
            /**
             * 月
             */
            if (repeatRateNotNull) {
                scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_MONTH, CronConstants.TYPE_INTERVAL, repeatRate, month));
            } else {
                scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_MONTH, CronConstants.TYPE_AT_TIME, String.valueOf(dayOfMonth)));
            }
            // 周设置为"?"
            scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_WEEK, CronConstants.TYPE_QUESTION_MARK));
        } else if (repeatRateUnit.equals("4")) {
            /**
             * 年
             */
            scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_DAY_OF_MONTH, CronConstants.TYPE_AT_TIME, String.valueOf(dayOfMonth)));
            scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_MONTH, CronConstants.TYPE_AT_TIME, String.valueOf(month)));
            if (repeatRateNotNull) {
                scheduleModels.add(new ScheduleModel(CronConstants.TIME_UNIT_YEAR, CronConstants.TYPE_INTERVAL, repeatRate, year));
            }

        }
        return createCron(scheduleModels);
    }

    public static String createCron(Set<ScheduleModel> scheduleModels) {
        String[] cronArr = {"*", "*", "*", "*", "*", "?", "*"};
        scheduleModels.forEach(i -> {
            Pair<String, Integer> timeUnit = i.getTimeUnit();
            Integer cronIndex = timeUnit.getValue();
            int type = i.getType();
            switch (type) {
                case 1:
                    cronArr[cronIndex] = "*";
                    break;
                case 2:
                    cronArr[cronIndex] = i.getStart() + "/" + i.getInterval();
                    break;
                case 3:
                    cronArr[cronIndex] = i.getAtTime();
                    break;
                case 4:
                    cronArr[cronIndex] = i.getFrom() + "-" + i.getTo();
                    break;
                case 5:
                    cronArr[cronIndex] = "?";
                default:
                    break;
            }
        });
        return StringUtils.join(cronArr, " ");
    }
}

