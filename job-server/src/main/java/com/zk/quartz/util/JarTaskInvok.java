package com.zk.quartz.util;


import cn.hutool.core.util.StrUtil;
import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.exception.TaskException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务可执行jar反射实现
 *
 * 
 */
@Slf4j
@Component("jarTaskInvok")
public class JarTaskInvok implements ITaskInvok {

	@Override
	public void invokMethod(SysJob sysJob) throws TaskException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		File jar = new File(sysJob.getExecutePath());
		processBuilder.directory(jar.getParentFile());
		List<String> commands = new ArrayList<>();
		commands.add("java");
		commands.add("-jar");
		commands.add(sysJob.getExecutePath());
		if (StrUtil.isNotEmpty(sysJob.getMethodParamsValue())) {
			commands.add(sysJob.getMethodParamsValue());
		}
		processBuilder.command(commands);
		try {
			Process process = processBuilder.start();
		} catch (IOException e) {
			log.error("定时任务jar反射执行异常,执行任务：{}", sysJob.getExecutePath());
			throw new TaskException("定时任务jar反射执行异常,执行任务：" + sysJob.getExecutePath());
		}
	}
}
