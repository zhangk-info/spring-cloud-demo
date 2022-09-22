package com.zk.quartz.controller;

import com.zk.commons.entity.Response;
import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.service.SysJobService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务调度表
 *
 * 
 * @date 2019-01-27 10:04:42
 */
@RestController
public class SysJobController {

    @Autowired
    private SysJobService sysJobService;

    /**
     * 保存定时任务
     * @param job
     * @return
     */
    @ApiOperation("保存定时任务")
    @PostMapping("/quartz/addOrUpdate")
    public Response<Integer> addOrUpdate(@RequestBody SysJob job) {
        sysJobService.addOrUpdateJob(job);
        return Response.successWithData(job.getJobId());
    }

    @ApiOperation("移除定时任务")
    @PostMapping("/quartz/removeJob")
    public Response removeJob(String jobGroup, String jobName) {
        sysJobService.removeJob(jobGroup, jobName);
        return Response.success();
    }
}
