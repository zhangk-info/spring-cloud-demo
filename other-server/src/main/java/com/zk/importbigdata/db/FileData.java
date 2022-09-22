package com.zk.importbigdata.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.concurrent.ConcurrentHashMap;


/**
 * 用于存储各种“同步表记录”的实体类。
 * Created by zhangk on 2020/11/23.
 *
 * @since 1.0
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class FileData {

    /**
     * 内部使用的逻辑主键。
     */
    private Long id;

    /**
     * 同步表记录的“处理状态”。
     */
    private FileStatus status;

    /**
     * 合并类型
     */
    private String category;

    /**
     * 开始合并时间
     */
    private String cleanStartTime;


    /**
     * 开始合并时间
     */
    private String startTime;

    /**
     * 合并完成时间
     */
    private String endTime;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 合并消息
     */
    private String msg;

    /**
     * ftp文件夹名称
     */
    private String dirName;

    /**
     * 上锁时间
     */
    private String lockTime;

    private String createDateTime;

//    @Transient
    @JsonIgnore
    public ConcurrentHashMap dataIdMap = new ConcurrentHashMap();

}