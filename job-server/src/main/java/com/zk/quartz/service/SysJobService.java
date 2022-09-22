package com.zk.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.quartz.bean.entity.SysJob;

/**
 * 定时任务调度表
 *
 * 
 * @date 2019-01-27 10:04:42
 */
public interface SysJobService extends IService<SysJob> {

    /**
     * 添加或者更新定时任务
     * @param sysJob 任务
     * @return 返回主键id
     */
    Integer addOrUpdateJob(SysJob sysJob);

    /**
     * 移除定时任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    void removeJob(String jobGroup, String jobName);

}
