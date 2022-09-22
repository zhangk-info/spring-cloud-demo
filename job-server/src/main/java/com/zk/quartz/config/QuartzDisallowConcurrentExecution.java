package com.zk.quartz.config;

import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.bean.enums.QuartzEnum;
import lombok.SneakyThrows;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 定时任务处理（禁止并发执行）
 *
 * DisallowConcurrentExecution 加了这个表示禁止并发执行 Job(任务)的执行时间[比如需要10秒]大于任务的时间间隔[Interval（5秒)]时，这个任务会等待任务执行完毕以后再执行
 *
 * @author ruoyi
 *
 */
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution implements Job
{
    @Autowired
    private QuartzInvokeFactory quartzInvokeFactory;

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext jobExecutionContext) {
        SysJob sysJob = (SysJob) jobExecutionContext.getMergedJobDataMap().get(QuartzEnum.SCHEDULE_JOB_KEY.getType());
        quartzInvokeFactory.init(sysJob, jobExecutionContext.getTrigger());
    }
}