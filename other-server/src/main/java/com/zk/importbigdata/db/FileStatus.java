package com.zk.importbigdata.db;

public enum FileStatus {
    /**
     * 文件读取中。
     */
    READING,
    /**
     * 文件读取失败。
     */
    READ_FAIL,
    /**
     * 文件读取成功。
     */
    READ_SUCCESS,
    /**
     * 数据清洗中
     */
    DATA_PROCESS,
    /**
     * 写入数据库完成。
     */
    DATA_COMPLETED,
    /**
     * 合并处理中。
     */
    IN_PROCESS,
    /**
     * 处理完成。
     */
    COMPLETED,

    /* 成功失败 是用于记录的 */
    /**
     * 等待处理
     */
    WAITTING,
    /**
     * 处理成功。
     */
    SUCCEED,
    /**
     * 处理失败。
     */
    FAIL,
    /**
     * 放弃执行
     */
    IGNORE,
    ;
}