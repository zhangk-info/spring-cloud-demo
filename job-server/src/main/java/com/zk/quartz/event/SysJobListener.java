package com.zk.quartz.event;

import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.util.TaskInvokUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Trigger;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;


/**
 * 异步监听定时任务事件
 */
@Slf4j
@AllArgsConstructor
public class SysJobListener {

    private TaskInvokUtil taskInvokUtil;

    @Async
    @Order
    @EventListener(SysJobEvent.class)
    public void executeOnce(SysJobEvent event) {
        SysJob sysJob = event.getSysJob();
        Trigger trigger = event.getTrigger();
        taskInvokUtil.invokMethod(sysJob, trigger);
    }
}
