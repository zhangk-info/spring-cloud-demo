package com.zk.readbigdata;

/**
 * “同步表记录”的服务层接口。
 * Created by zhangk on 2020/11/23.
 *
 * @author zhangk
 * @since 1.0
 */
public interface FileDataService {


    /**
     * 遍历一次ftp服务器文件
     * 存储文件读取记录
     * 缓存文件全量数据行
     */
    void ftpFileRead();

    /**
     * 每10分钟遍历一次 需要数据清洗的文件记录
     * 需要清洗的文件记录：
     * 状态为“文件读取成功”的
     * 状态为“数据清洗中”，但在预期时间的5倍时长尚未清洗完成的（如没有默认一条1秒）
     */
    void fileSourceDataClean();


    void fileDelete();

}