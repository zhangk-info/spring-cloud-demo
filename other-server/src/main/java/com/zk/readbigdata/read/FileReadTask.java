package com.zk.readbigdata.read;


import com.zk.readbigdata.FileDataService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件数据采集任务
 *
 * @author zhangk
 * @createDate 2020/11/24
 */
@Component
@ConditionalOnProperty(name = "ftp.read", havingValue = "true")
public class FileReadTask {

    private final FileDataService fileDataService;

    public FileReadTask(FileDataService fileDataService) {
        this.fileDataService = fileDataService;
    }

    /**
     * 每2分钟遍历一次ftp服务器文件
     */
    @Scheduled(fixedDelay = 120000)
    public void FileReadTask() {
        fileDataService.ftpFileRead();
    }
}
