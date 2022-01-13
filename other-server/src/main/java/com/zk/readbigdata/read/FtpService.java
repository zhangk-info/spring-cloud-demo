package com.zk.readbigdata.read;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import com.alibaba.excel.EasyExcel;
import com.zk.commons.exception.ServiceException;
import com.zk.configuration.redis.RedisService;
import com.zk.readbigdata.constant.FileCategory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ftp文件处理工具类
 */
@Slf4j
@Component
public class FtpService {

    // ftp地址
    @Value("${ftp.path}")
    private String ftpPath;
    // ftp端口
    @Value("${ftp.port}")
    private Integer ftpPort;
    // ftp用户
    @Value("${ftp.user}")
    private String ftpUser;
    // ftp密码
    @Value("${ftp.password}")
    private String ftpPassword;
    // ftp接收区
    @Value("${ftp.space}")
    private String ftpSpace;

    /**
     * 从ftp文件中读取到数据行
     *
     * @param fileName
     * @return
     */
    public void getDataFromFtpFile(String dayStr, String fileName, String key, RedisService redisService) {
        // 通过文件名得到该文件的类型
        FileCategory fileCategory;
        try {
            fileCategory = FileCategory.getByFileName(fileName);
        } catch (Exception e) {
            throw e;
        }

        // 建立连接
        /*
         * 主动模式：服务器发起21端口去访问客户端的随机端口，并通过服务器的20端口来传输数据。
         * 被动模式：客户端发起连接服务器的21端口，然后随机开启一个数据连接端口进行数据传输。
         * 主动模式传送数据时是“服务器”连接到“客户端”的端口；被动模式传送数据是“客户端”连接到“服务器”的端口。 主动模式需要客户端必须开放端口给服务器
         * 记踩坑：默认的是主动模式，需要开启服务器的20端口
         */
        Ftp ftp = null;
        try {
            ftp = new Ftp(ftpPath, ftpPort, ftpUser, ftpPassword, CharsetUtil.CHARSET_UTF_8, (String) null, (String) null, FtpMode.Passive);
        } catch (Exception e) {
            throw new ServiceException("ftp连接失败");
        }
        File file = null;
        try {
            LocalDateTime begin = LocalDateTimeUtil.now();
            // 下载到临时文件
            file = FileUtil.createTempFile(null);

            String dateStr = dayStr;// DateUtil.format(DateUtil.yesterday(), "yyyyMMdd");
            ftp.download(ftpSpace + dateStr, fileName, file);
            ftp.close();
//            FileUtil.copy(new File("D://S_EDW_NCB_CBKCIFA.txt"), file, true);
            log.info(" 保存ftp文件 [" + fileName + "] 到临时文件成功 临时文件路径为:  " + file.getPath());

            Integer size = 0;
            // 如果文件大于100M 采用边读边写的形式对文件数据进行读取
            if (!fileCategory.getFileType().equals("excel")) {
                log.info("[" + fileName + "] 是txt文件");
                if (Files.size(file.toPath()) > 100 * 1024 * 1024) {
                    log.info("[" + fileName + "] 文件大于100M");
                    // 如果是txt文件
                    // 根据临时文件读取数据
                    BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis, fileCategory.getCharSet()), 50 * 1024 * 1024);// 用5M的缓冲读取文本文件

                    String line;
                    List<String> temp = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        // 边读边写保存数据在
                        size++;
                        temp.add(line);
                        if (temp.size() > 199999) {
                            log.info("[" + fileName + "] 写入一个20万行文件，当前共 " + size + " 行。");
                            redisService.lAdd(key, temp, 2 * 24 * 60 * 60);
                            temp.clear();
                            // 等半秒 减小服务器压力
                            try {
                                TimeUnit.MILLISECONDS.sleep(1000);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                    // 最后有 < 199999 的也要保存
                    if (temp.size() > 0) {
                        redisService.lAdd(key, temp, 2 * 24 * 60 * 60);
                        temp.clear();
                    }
                } else {
                    List<String> string = null;
                    // 如果是txt文件
                    // 根据临时文件读取数据
                    string = Files.readAllLines(Path.of(file.getPath()), Charset.forName(fileCategory.getCharSet()));
                    if (CollectionUtil.isNotEmpty(string)) {
                        // 全量保存数据
                        redisService.lAdd(key, string, 2 * 24 * 60 * 60);
                    }
                    size = string.size();
                }
            } else {
                log.info("[" + fileName + "] 是excel文件");

                // 如果是excel文件
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                FileDataExcelReadListener listener = new FileDataExcelReadListener(size, key, redisService);
                EasyExcel.read(file, listener).sheet().doRead();

            }

            // 得到已耗时 毫秒
            Long costTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - begin.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            log.error("文件 " + dayStr + "/" + fileName + "的数据行读取结束 read end ，共 " + size + " 行,耗时毫秒：" + costTime);
            log.error("文件 " + dayStr + "/" + fileName + "的数据行存入redis, key为 " + key);


        } catch (Exception e) {
            log.error("读取数据文件失败", e);
            throw new ServiceException("读取数据文件失败！");
        } finally {
            try {
                if (file != null) {
                    log.error("删除文件： " + file.getPath());
                    FileUtil.del(file);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 从ftp获取今天同步下来的文件
     *
     * @return
     */
    public LinkedHashMap<String, List<FTPFile>> listFileOfAllFromFtp() {

        LinkedHashMap<String, List<FTPFile>> files = new LinkedHashMap<>();
        /*
         * 主动模式：服务器发起21端口去访问客户端的随机端口，并通过服务器的20端口来传输数据。
         * 被动模式：客户端发起连接服务器的21端口，然后随机开启一个数据连接端口进行数据传输。
         * 主动模式传送数据时是“服务器”连接到“客户端”的端口；被动模式传送数据是“客户端”连接到“服务器”的端口。 主动模式需要客户端必须开放端口给服务器
         * 记踩坑：默认的是主动模式，需要开启服务器的20端口
         */
        log.info(" ftp 连接信息： -- " + ftpPath + " : " + ftpPort + " / " + ftpUser + " / " + ftpPassword);
        Ftp ftp = null;
        try {
            ftp = new Ftp(ftpPath, ftpPort, ftpUser, ftpPassword, CharsetUtil.CHARSET_UTF_8, (String) null, (String) null, FtpMode.Passive);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("-- -- ftp连接失败");
            throw new ServiceException("ftp连接失败");
        }

        // 改 得到所有文件按照日期排序 主表第一排序
        FTPFile[] allDirs = ftp.lsFiles(ftpSpace);
        // 顺序排序
        List<FTPFile> dirList = Arrays.stream(allDirs).filter(t -> t.isDirectory()).collect(Collectors.toList());
        allDirs = dirList.stream().sorted((t1, t2) -> {
            try {
                if (DateUtil.parse(t1.getName(), "yyyyMMdd").getTime() < DateUtil.parse(t2.getName(), "yyyyMMdd").getTime()) {
                    return -1;
                }
            } catch (Exception e) {
                return -1;
            }
            return 1;
        }).collect(Collectors.toList()).toArray(new FTPFile[dirList.size()]);
        // 遍历所有文件夹
        for (FTPFile dir : allDirs) {

            if (dir.isDirectory()) {
                // !important 跳过今天的文件夹 用来解决今天的文件子表先传输完成 主表后传输完成 造成子表先于主表处理时的数据遗漏bug
                String todayStr = DateUtil.format(new Date(), "yyyyMMdd");
                if (dir.getName().contains(todayStr)) {
                    continue;
                }

                // 得到所有传输完成的文件
                Map<String, String> ftpFileNameMap = Arrays.stream(ftp.lsFiles(ftpSpace + dir.getName())).collect(Collectors.toMap(t -> t.getName(), t -> t.getName()));

                // 存储的时候是每天的文件放到一个文件夹下
                List<FTPFile> oneDayFiles = ftp.lsFiles(ftpSpace + dir.getName(), t -> {
                    Boolean flag = false;
                    // 如果文件是今天的 并且有一个同名文件是.ok的即传输完成的
                    if (ftpFileNameMap.containsKey(t.getName() + ".ok")) {
                        flag = true;
                    }
                    return flag;
                });
                // !important 把主表排在前面 用于主表先采集
                oneDayFiles = oneDayFiles.stream().sorted((t1, t2) -> {
                    if (t1.getName().contains("mainTable")) {
                        return -1;
                    }
                    return 1;
                }).collect(Collectors.toList());

                files.put(dir.getName(), oneDayFiles);
            }
        }

        try {
            ftp.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return files;
    }

    public void deletedByFileNameAndDate(String dateStr, String fileName) {
        /*
         * 主动模式：服务器发起21端口去访问客户端的随机端口，并通过服务器的20端口来传输数据。
         * 被动模式：客户端发起连接服务器的21端口，然后随机开启一个数据连接端口进行数据传输。
         * 主动模式传送数据时是“服务器”连接到“客户端”的端口；被动模式传送数据是“客户端”连接到“服务器”的端口。 主动模式需要客户端必须开放端口给服务器
         * 记踩坑：默认的是主动模式，需要开启服务器的20端口
         */
        Ftp ftp = null;
        try {
            ftp = new Ftp(ftpPath, ftpPort, ftpUser, ftpPassword, CharsetUtil.CHARSET_UTF_8, (String) null, (String) null, FtpMode.Passive);
        } catch (Exception e) {
            throw new ServiceException("ftp连接失败");
        }
        log.error("删除文件 ：" + ftpSpace + dateStr + "/" + fileName);
        ftp.delFile(ftpSpace + dateStr + "/" + fileName);
        log.error("删除文件 ：" + ftpSpace + dateStr + "/" + fileName + ".ok");
        ftp.delFile(ftpSpace + dateStr + "/" + fileName + ".ok");
        if (ftp.lsFiles(ftpSpace + dateStr).length == 0) {
            log.error("删除目录 ：" + ftpSpace + dateStr);
            ftp.delDir(ftpSpace + dateStr);
        }

        try {
            ftp.close();
        } catch (Exception e) {

        }
    }
}
