package com.zk.quartz.config;

import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.bean.enums.QuartzEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 定时任务处理（允许并发执行）
 *
 * <p>
 * 动态任务工厂
 */
@Slf4j
public class QuartzFactory implements Job {

    @Autowired
    private QuartzInvokeFactory quartzInvokeFactory;


    @Override
    @SneakyThrows
    public void execute(JobExecutionContext jobExecutionContext) {
        SysJob sysJob = (SysJob) jobExecutionContext.getMergedJobDataMap().get(QuartzEnum.SCHEDULE_JOB_KEY.getType());
        quartzInvokeFactory.init(sysJob, jobExecutionContext.getTrigger());
    }
}
