package com.zk.commons.exception;

public enum ErrorCode {
	//权限相关
	NO_DATA_ACCESS_POWER(1000,"无数据访问权限"),
	NO_API_ACCESS_POWER(1001,"无接口访问权限"),
	//数据相关
	DATA_NOT_EXIST(100,"%s数据已清除"),
	DATA_EXIST(101,"数据已存在"),
	DATA_TABLE_USED(102,"%s占用中"),
	DATA_GROUP_USED(103,"%s占用中"),
	DATA_USED(104,"数据占用中,%s"),
	DATA_SYN_ERROR(104,"数据同步错误"),
	DATA_DISABLED(105,"%s被禁用"),
	NO_TABLES(106,"数据集下面没有可用表"),
	FORM_NO_DATAS(107,"主表单没有任何数据"),
	NO_PK(108,"主键为空"),
	PARAMS_IS_NULL(109,"参数为空%s"),
	NO_TABLE(110,"数据集下面没有表%s"),
	EX_FILE_NOT_FOUNT(111,"文件不存在"),
	SERVER_ERROR(112,"服务器错误"),
	NO_LOGIN(113,"登录信息过期或者未登录"),
	//配置相关
	CONFIG_NO_PK(200,"%s没有配置主键"),
	CONFIG_NO_FK(201,"%s没有配置外键"),
	CONFIG_NO_COLUMN(202,"没有相应列"),
	CONFIG_NO_PRIMARY_TABLE(203,"没有配置主表"),
	NO_COLUMN(204,"没有列%s"),
	NO_ROLES(205,"没有配置角色"),
	//文件导出相关
	NO_FILE(300,"无文件"),
	FS_SERVER_ERROR(301,"文件服务器错误 "),
	MODULE_NULL(302,"请配置默认上传模板"),
	//SQL
	DB_CONN(400,"数据库连接失败"),
	SYN_TABLE_ERROR(406,"同步表错误"),
	TABLE_NOT_EXIST(405,"%s表不存在"),
	SQL_ERROR(401,"SQL执行错误%s"),
	CONFIG_ERROR(402,"%s配置信息错误"),
	FUNC_ERROR(403,"存储过程执行"),
	RUNNING(404,"任务执行中"),
	//用户相关
	OLD_PWD_ERROR(500, "旧密码错误"),
	//自定义消息错误
	CUSTOM_ERR_MSG(600, "%s"),
	PARAMS_ERR(700, "%s参数错误"),
	//流程相关
	PROCESS_INSTANCE_ERR(800,"流程实例化错误"),
	UPDATE_PROCESS_INSTANCE_ERR(801,"更新业务流程实例错误"),
	TASK_NULL(802,"当前任务已清除"),

	//导入相关
	FILE_ERROR(900,"文件错误"),
	FILE_SAVE(902,"文件转存失败"),
	TABLE_CONFIG_ERROR(901,"%S")
	;
	public Integer code;
	public String message;

	ErrorCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
