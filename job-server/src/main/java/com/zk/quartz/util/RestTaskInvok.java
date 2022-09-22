package com.zk.quartz.util;

import cn.hutool.json.JSONUtil;
import com.zk.commons.entity.Response;
import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.exception.TaskException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 定时任务rest反射实现
 */
@Slf4j
@Component("restTaskInvok")
@AllArgsConstructor
public class RestTaskInvok implements ITaskInvok {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void invokMethod(SysJob sysJob) throws TaskException {
        Response r = restTemplate.getForObject(sysJob.getExecutePath(), Response.class);
        if (r.getMeta().getCode() != 0) {
            log.error("定时任务restTaskInvok异常,执行任务：{}", sysJob.getExecutePath());
            throw new TaskException("定时任务restTaskInvok业务执行失败,任务：" + sysJob.getExecutePath() + ";错误原因：" + JSONUtil.toJsonStr(r));
        }
    }
}
