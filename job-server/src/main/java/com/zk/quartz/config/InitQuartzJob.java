//package com.zk.quartz.config;
//
//import com.zk.quartz.bean.enums.QuartzEnum;
//import com.zk.quartz.service.SysJobService;
//import com.zk.quartz.util.TaskUtil;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.Scheduler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
///**
// * <p>
// * 初始化加载定时任务:将数据库中的所有数据查出来，该启动的启动，该暂停的暂停
// */
//@Slf4j
//@Configuration
//@AllArgsConstructor
//public class InitQuartzJob {
//    private final SysJobService sysJobService;
//    private final TaskUtil taskUtil;
//    private final Scheduler scheduler;
//
//    @Bean
//    public void customize() {
//        sysJobService.list().forEach(sysjob -> {
//            if (QuartzEnum.JOB_STATUS_RELEASE.getType().equals(sysjob.getJobStatus())) {
//                taskUtil.removeJob(sysjob, scheduler);
//            } else if (QuartzEnum.JOB_STATUS_RUNNING.getType().equals(sysjob.getJobStatus())) {
//                taskUtil.resumeJob(sysjob, scheduler);
//            } else if (QuartzEnum.JOB_STATUS_NOT_RUNNING.getType().equals(sysjob.getJobStatus())) {
//                taskUtil.pauseJob(sysjob, scheduler);
//            } else {
//                taskUtil.removeJob(sysjob, scheduler);
//            }
//        });
//    }
//}
