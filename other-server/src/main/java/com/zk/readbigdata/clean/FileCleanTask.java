package com.zk.readbigdata.clean;

import com.zk.readbigdata.FileDataService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件数据清洗任务
 *
 * @author zhangk
 * @createDate 2020/11/24
 */
@Component
@ConditionalOnProperty(name = "ftp.clean", havingValue = "true")
public class FileCleanTask {

    private FileDataService fileDataService;

    public FileCleanTask(FileDataService fileDataService) {
        this.fileDataService = fileDataService;
    }

    /**
     * 每3分钟遍历一次 需要数据清洗的文件记录
     * 需要清洗的文件记录：
     * 状态为“文件读取成功”的
     * 状态为“数据清洗中”，但在预期时间的5倍时长尚未清洗完成的（如没有默认一条0.5秒）
     */
//    @Scheduled(fixedDelay = 180000)
    @Scheduled(fixedDelay = 120000)
    public void FileReadTask() {
        fileDataService.fileSourceDataClean();
    }
}
