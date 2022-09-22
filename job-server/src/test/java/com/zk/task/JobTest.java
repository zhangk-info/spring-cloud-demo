package com.zk.task;

import com.zk.ControllerTest;
import com.zk.JobApplication;
import com.zk.RequestTypeEnum;
import com.zk.quartz.bean.entity.SysJob;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = JobApplication.class)
public class JobTest extends ControllerTest {

    @Test
    public void save() throws Exception {
        SysJob sysJob = new SysJob();

        this.execute(RequestTypeEnum.POST, "/quartz/addOrUpdate", sysJob);
    }
}
