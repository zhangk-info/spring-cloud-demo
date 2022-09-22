package com.zk.readbigdata;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ftp文件 删除 任务
 *
 * 
 * @createDate 2020/11/24
 */
@Component
@ConditionalOnProperty(name = "ftp.clean", havingValue = "true")
public class FileDeleteTask {

    private FileDataService fileDataService;

    public FileDeleteTask(FileDataService fileDataService) {
        this.fileDataService = fileDataService;
    }

    /**
     * 每天0点到1点 每19分钟触发
     * 删除3天前已经完成了的文件 状态为completed
     * 对于没有参与合并的表，删除7天前清洗完成的文件
     */
    @Scheduled(cron = "0 0/19 00-01 * * ?")
//    @Scheduled(fixedDelay = 20000)
    public void FileReadTask() {
        fileDataService.fileDelete();
    }
}
