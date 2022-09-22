package com.zk.importbigdata.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 一次清洗的记录 data oldData
 * Created by zhangk on 2020/11/23.
 *
 * @since 1.0
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class FileDataRecords {

    /**
     * 内部使用的逻辑主键。
     */
    private Long id;

    /**
     * 合并类型
     */
    private String category;

    /**
     * 来自同步表的主键或逻辑主键
     */
    private String dataId;

    /**
     * 同步表记录的“原始数据”，没有经过任何处理，仅仅是经过“从通过过来的文件读取”这个动作得到的数据，以Json的形式存储。
     */
    private String data;

    /**
     * 上一次同步的“老原始数据”
     */
    private String oldData;

    /**
     * 排序号
     */
    private long orderNumber;

    /**
     * 同步表记录的“处理状态”。
     */
    private FileStatus status;

    /**
     * 合并失败原因
     */
    private String reason;

    private String createDateTime;

}