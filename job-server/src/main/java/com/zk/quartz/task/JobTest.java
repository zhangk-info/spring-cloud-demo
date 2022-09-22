package com.zk.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zk.quartz.bean.constants.JobConstants;
import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.bean.enums.JobTypeQuartzEnum;
import com.zk.quartz.bean.util.CronUtil;
import com.zk.quartz.service.SysJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @date 2020/12/9 10:15
 * @description: 消息通知定时任务
 */
@Slf4j
@Component
public class JobTest {

    @Autowired
    private SysJobService sysJobService;

    /**
     * 添加定时任务。注意jobGroup + jobName组合构成唯一值
     * jobGroup jobName为 timeFieldMsgHandler_[id]
     *
     * @param jobGroup
     * @param primaryId         主表主键
     * @param executeTime       执行时间
     * @param methodParamsValue 传递参数
     */
    private void addJob(String jobGroup, String primaryId, Date executeTime, String methodParamsValue) {
        String jobName = "timeFieldMsgHandler_" + primaryId;
        String cronExpression = CronUtil.createCron("0", executeTime, null, null);
        // 判断定时任务中是否有unitId + primaryId为主键且时间和当前的时间相同的定时任务
        SysJob sysJob = sysJobService.getOne(new LambdaQueryWrapper<SysJob>()
                .eq(SysJob::getJobGroup, jobGroup)
                .eq(SysJob::getJobName, jobName)
                .ne(SysJob::getJobStatus, "4"));
        if (Objects.nonNull(sysJob)) {
            if (!sysJob.getCronExpression().equals(cronExpression)) {
                // 如果新计算出来的表达式和原来的表达式不一样，那么删除原来的并创建新的 设置overwrite-existing-jobs: true之后可以不用删除直接覆盖已有
                log.debug("新计算出来的表达式【" + cronExpression + "】和原来的表达式【" + sysJob.getCronExpression() + "】不一样，那么删除原来的并创建新的");
                sysJobService.removeJob(jobGroup, jobName);
            } else {
                return;
            }
        }
        // 创建新的
        sysJob = new SysJob();
        sysJob.setCronExpression(cronExpression);
        sysJob.setJobGroup(jobGroup);
        sysJob.setJobName(jobName);
        sysJob.setJobType(JobTypeQuartzEnum.SPRING_BEAN.getType());
        sysJob.setClassName(JobConstants.NOTIFY_CONFIG_SERVICE);
        sysJob.setMethodName(JobConstants.NOTIFY_CONFIG_SERVICE_METHOD_NAME);
        sysJob.setMisfirePolicy(JobConstants.misfire_Policy_NOE);
        sysJob.setMethodParamsValue(methodParamsValue);
//        sysJob.setEndTime(sysSchedule.getExpiryDate());
        sysJobService.addOrUpdateJob(sysJob);
        log.debug("创建新的定时任务成功，jobId: " + sysJob.getJobId());
    }

}
