package com.zk.quartz.util;


import com.zk.quartz.bean.entity.SysJob;
import com.zk.quartz.exception.TaskException;

/**
 * 定时任务反射实现接口类
 *
 * 
 */
public interface ITaskInvok {

	/**
	 * 执行反射方法
	 *
	 * @param sysJob 配置类
	 * @throws TaskException
	 */
	void invokMethod(SysJob sysJob) throws TaskException;
}
