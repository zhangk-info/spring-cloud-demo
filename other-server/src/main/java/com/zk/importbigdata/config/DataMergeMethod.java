package com.zk.importbigdata.config;


import com.zk.importbigdata.format.RowFormat;

/**
 * 使用 DataMergeExecutor 必须实现的接口方法
 * 1. RowFormat
 * 2. 单行数据合并业务
 * 3. 逻辑主键检查 非必须
 *
 * @param <T>
 */
public interface DataMergeMethod<Object> extends RowFormat {

    /**
     * 实现单个数据行合并的业务需求
     *
     * @param t
     */
    void saveFromRecords(Object t);

}
