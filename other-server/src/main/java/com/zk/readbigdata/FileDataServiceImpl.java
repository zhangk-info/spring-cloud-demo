package com.zk.readbigdata;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.zk.commons.exception.ServiceException;
import com.zk.configuration.redis.RedisService;
import com.zk.importbigdata.db.FileData;
import com.zk.importbigdata.db.FileStatus;
import com.zk.readbigdata.clean.DataCleanHelper;
import com.zk.readbigdata.constant.FileCategory;
import com.zk.readbigdata.read.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link FileDataService}的实现类。
 * Created by zhangk on 2020/11/23.
 *
 * 
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = {ServiceException.class})
public class FileDataServiceImpl implements FileDataService {


    @Autowired
    private FtpService ftpService;
//    private ObjectMapper objectMapper;

    // 开始采集清洗合并子表的日期
    @Value("${ftp.first_dir}")
    private String firstDir;
    // 线程数
    @Value("${crm.threadcount}")
    private Integer threadCount;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 遍历一次ftp服务器文件
     * 存储文件读取记录
     * 缓存文件全量数据行
     */
    @Override
    public void ftpFileRead() {

        // 1.得到今天的所有ftp已经传输完成的文件 改 得到所有的ftp文件
        LinkedHashMap<String, List<FTPFile>> ftpDaysFiles = ftpService.listFileOfAllFromFtp();
//        log.error("1. 找到" + ftpDaysFiles.size() + "个ftp dir");

        for (String dayStr : ftpDaysFiles.keySet()) {
            List<FTPFile> ftpFiles = ftpDaysFiles.get(dayStr);
//            log.error("1.1. 文件夹 " + dayStr + " 有 " + ftpFiles.size() + "个ftp file");

            // 得到一天已经存在的fileData数据
            List<FileData> onedayFileDatas = new ArrayList<>();// findByDate(dayStr);
            Map<FileCategory, FileData> fileDataCompletedMap = new HashMap<>();
            if (CollUtil.isNotEmpty(onedayFileDatas)) {
                onedayFileDatas.forEach(onedayFileData -> {
                    String fileName = onedayFileData.getFileName();
                    if (StrUtil.isNotEmpty(fileName)) {
                        // 通过文件名得到该文件的类型
                        FileCategory fileCategory;
                        try {
                            fileCategory = FileCategory.getByFileName(fileName);

                            // 得到已耗时 毫秒
                            Long costTime = LocalDateTimeUtil.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - LocalDateTimeUtil.parse(onedayFileData.getCreateDateTime()).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                            // 如果已耗时 大于30分钟 就重新读取 30分钟可以读取几十上百G大小的文件了 长时间没有读取结束
                            if ((onedayFileData.getStatus().equals(FileStatus.READING)) && costTime > 30 * 60 * 1000) {
                                log.error("find a file that cost time more than 30 min to read " + onedayFileData.getDirName() + "/" + onedayFileData.getFileName());
                                // todo deleteAllByFileData_Id(onedayFileData.getId());
                                // todo deleteById(onedayFileData.getId());
                            } else {
                                fileDataCompletedMap.put(fileCategory, onedayFileData);
                            }
                        } catch (Exception e) {
                            //不处理 只是跳过该条 不放入Map中
                        }
                    }
                });
            }

            // 1.x 如果有超过2个文件在读取中，那么等那个读取完成了再执行其他的读取
            Integer readingFileCount = 2;// todo findAllByStatus(FileStatus.READING).size()
            if (readingFileCount >= 2) {
                log.error("1.x 有超过2个文件在读取中，那么等那个读取完成了再执行其他的读取");
                continue;
            }

            // 1.x  采集超过2个文件 先清理 清理完成 删除redis  再采集
            Integer readSuccessFileCount = 2;// todo // todo findAllByStatus(FileStatus.READ_SUCCESS).size() + // todo findAllByStatus(FileStatus.DATA_PROCESS).size()
            if (readSuccessFileCount >= 2) {
                log.error("1.x 采集超过2个文件 先清理 清理完成 删除redis 再采集");
                continue;
            }

            // 2.遍历并开启新线程处理
            for (FTPFile t : ftpFiles) {

                // 3.判断文件是否处理过
                String fileName = t.getName();
                FileCategory fileCategory = null;
                if (StrUtil.isNotEmpty(fileName)) {
                    // 通过文件名得到该文件的类型
                    try {
                        fileCategory = FileCategory.getByFileName(fileName);
                    } catch (Exception e) {
                        continue;
                    }
                    // 处理过了，直接跳过该文件
                    if (fileDataCompletedMap.containsKey(fileCategory)) {
                        continue;
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    Date dateOfFile;
                    try {
                        dateOfFile = dateFormat.parse(dayStr);
                    } catch (ParseException e) {
                        continue;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateOfFile);

                    // 如果文件日期在第一天之前且不是主表，并且不是指定日期的文件，跳过 如果文件日期不是周五或者周六 跳过
                    if ((Integer.parseInt(dayStr) < Integer.parseInt(firstDir) && !fileCategory.getTableCode().equals("mainTable"))
                            || (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && !fileCategory.getTableCode().equals("mainTable")) /*如果文件日期不是周五或者周六且不是主表 跳过*/
                    ) {

                        // 新建fileData
                        FileData fileData = new FileData();
                        fileData.setCreateDateTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                        fileData.setFileName(t.getName());
                        fileData.setFileSize(t.getSize());
                        fileData.setDirName(dayStr);

                        FileCategory category = null;
                        try {
                            String name = t.getName();
                            // 通过文件名得到该文件的类型
                            try {
                                category = FileCategory.getByFileName(name);
                            } catch (Exception e) {
                                continue;
                            }

                            fileData.setCategory(category == null ? null : category.getTableCode());
                            // 标记放弃执行的文件为执行成功
                            fileData.setStatus(FileStatus.IGNORE);
                            fileData.setEndTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                            fileData.setMsg("放弃执行");
                            fileData.setStartTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                            fileData.setEndTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                            //todo save(fileData);

                            continue;
                        } catch (Exception e) {
                            log.error("保存放弃执行的文件记录失败 -- 不处理" + e.getMessage());
                        }
                    }
                }

                // 得到  主表  的记录 如果没有主表直接处理 如果有主表 等待主表数据处理完成再处理
                List<FileData> mianTableToday = onedayFileDatas.stream().filter(o -> o.getCategory().equals("mainTable")).collect(Collectors.toList());
                if (fileCategory.getTableCode() != "mainTable") {
                    if (CollectionUtils.isEmpty(mianTableToday)) {
                        // 如果主表有数据待处理 那么等主表先处理完成再处理字表
//                        log.error("没有得到有    主表    的待处理记录 ， 所以本次处理退出，等待主表先处理。");
//                        continue;
                    } else {
                        FileData fileData = mianTableToday.get(0);
                        if (!fileData.getStatus().equals(FileStatus.DATA_COMPLETED) && !fileData.getStatus().equals(FileStatus.IN_PROCESS) && !fileData.getStatus().equals(FileStatus.COMPLETED)) {
                            // 如果主表有数据待清洗 那么等主表先清洗完成再处理字表
                            log.error("clean ： find main 得到有    主表    的待清洗记录，文件名为： " + fileData.getDirName() + "/" + fileData.getFileName() + ",所以本次清洗退出，等待主表先清洗。");
                            continue;
                        }
                    }
                }


                new Thread(() -> {
                    // 新建fileData
                    FileData fileData = new FileData();
                    fileData.setCreateDateTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                    fileData.setFileName(t.getName());
                    fileData.setFileSize(t.getSize());
                    fileData.setDirName(dayStr);

                    FileCategory category = null;
                    try {
                        String name = t.getName();
                        // 通过文件名得到该文件的类型
                        try {
                            category = FileCategory.getByFileName(name);
                        } catch (Exception e) {
                            throw e;
                        }

                        fileData.setCategory(category == null ? null : category.getTableCode());
                        fileData.setStatus(FileStatus.READING);
                        // todo save(fileData);

                        FileCategory finalFileCategory = category;

                        // 开辟新线程是为了防止文件读取时间超过线程时间
                        new Thread(() -> {
                            FileData temp = null;
                            // 这里自旋获取fileData是为了让主线程先事务提交成功才继续执行
                            while (temp == null) {
                                temp = new FileData();// todo getOne(fileData.getId());
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                            log.error("read begin 读取文件 " + dayStr + "/" + t.getName() + "的数据行开始");

                            String dateStr = dayStr;//DateUtil.format(DateUtil.yesterday(), "yyyyMMdd");
                            String key = "crm:file:" + finalFileCategory.getTableCode() + ":" + dateStr;
                            // 清空redis存储的内容
                            redisService.del(key);

                            // 4.读取文件的数据行
                            // 5.将行数据存入redis中并标记过期时间为2天
                            ftpService.getDataFromFtpFile(dayStr, t.getName(), key, redisService);


                            // 6.新建记录标记为文件读取完成
                            temp.setStatus(FileStatus.READ_SUCCESS);
                            // todo save(temp);
                        }).start();

                    } catch (Exception e) {
                        // 文件处理失败 这里可能是“识别类别失败”或者"读取文件行数据失败" 新建记录并标记为文件读取失败
                        fileData.setStatus(FileStatus.READ_FAIL);
                        fileData.setEndTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                        if (e instanceof ServiceException) {
                            fileData.setMsg(((ServiceException) e).getMessage());
                        } else {
                            fileData.setMsg(e.getMessage());
                        }
                        fileData.setStartTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                        fileData.setEndTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                        // todo save(fileData);
                    }

                }).start();

                //一次只处理一个 ftp被动模式的放开端口有限
                return;
            }

        }

    }


    /**
     * 每10分钟遍历一次 需要数据清洗的文件记录
     * 需要清洗的文件记录：
     * 状态为“文件读取成功”的
     * 状态为“数据清洗中”，但在预期时间的5倍时长尚未清洗完成的（如没有默认一条0.5秒）
     */
    @Override
    public void fileSourceDataClean() {

        // 1.得到需要清洗的文件记录：
        List<FileData> fileDatas = new ArrayList<>();

        // 1.1.状态为“数据清洗中”，但在 大于10小时 尚未清洗完成的 改成读取成功
        List<FileData> processDatas = new ArrayList<>();// todo findAllByStatus(FileStatus.DATA_PROCESS);

        for (FileData processData : processDatas) {
            // 得到已耗时 毫秒
            Long costTime = LocalDateTimeUtil.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - LocalDateTimeUtil.parse(processData.getCleanStartTime()).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // 如果已耗时  大于10小时
            if (costTime > 36000000L) {
                log.error("find a file that cost time more than 300 min to clean" + processData.getDirName() + "/" + processData.getFileName());
                try {
                    // 设置时间，防止其它线程重复进入 = 加锁
                    processData.setCleanStartTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                    // todo save(processData);

                    // 处理
                    // todo deleteAllByFileData_Id(processData.getId());

                    processData = // todo getOne(processData.getId());
                            processData.setStatus(FileStatus.READ_SUCCESS);
                    // todo save(processData);
                } catch (Exception e) {
                    // 这里可能1分钟没处理完成 后面多进来了几个线程 造成
                    //  Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)
                    // 不解决
                }
                log.error("change file to read_success use to clean again " + processData.getDirName() + "/" + processData.getFileName());
            }
        }

        // 1.x 如果有超过1个文件在清洗中，那么等那个清洗完成了再执行其他的清洗
        Integer dataProcessFileCount = 0;// todo findAllByStatus(FileStatus.DATA_PROCESS).size()
        if (dataProcessFileCount >= 1) {
            log.error("1.x 有超过1个文件在清洗中，那么等那个清洗完成了再执行其他的清洗");
            return;
        }


        // 1.2.状态为“文件读取成功”的
        fileDatas.addAll(new ArrayList<>());// todo findAllByStatus(FileStatus.READ_SUCCESS));

        if (CollectionUtil.isNotEmpty(fileDatas)) {
            // 改 一个一个的清理
            FileData fileData = fileDatas.get(0);
//            fileDatas.forEach(t -> {
            // 开始清洗文件
            Thread t = new Thread(() -> {
                log.error("begin clean 开始清理文件 " + fileData.getFileName());
                dataClean(fileData);
                log.error("end clean 清理文件 " + fileData.getFileName() + " 结束");
            });
            t.setPriority(4);
            t.start();
//            });
        }

    }

    @Override
    public void fileDelete() {
        // 删除7天前合并完成的文件
        List<FileData> completedOut7Days = new ArrayList<>();// todo findByCompletedOut7Days();
        completedOut7Days.forEach(t -> {
            String dateStr = t.getDirName();//LocalDateTimeUtil.format(LocalDateTimeUtil.offset(t.getCreateDateTime(), -1, ChronoUnit.DAYS), "yyyyMMdd");
            ftpService.deletedByFileNameAndDate(dateStr, t.getFileName());
        });

        // 对于没有参与合并的表，删除7天前清洗完成的文件
        List<FileData> dataCompletedOut7DaysAndNotInMerge = new ArrayList<>(); // todo findByDataCompletedOut7DaysAndNotInMerge();
        dataCompletedOut7DaysAndNotInMerge.forEach(t -> {
            String dateStr = t.getDirName();//LocalDateTimeUtil.format(LocalDateTimeUtil.offset(t.getCreateDateTime(), -1, ChronoUnit.DAYS), "yyyyMMdd");
            ftpService.deletedByFileNameAndDate(dateStr, t.getFileName());
        });

    }

    /**
     * 单个文件清理子线程
     *
     * @param fileData 单个文件记录
     */
    public void dataClean(FileData fileData) {


        // 记录一个开始时间用于统计平均耗时作为异常判断条件
        Long cleanStartTime = LocalDateTimeUtil.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        // 首先设置开始清洗时间
        fileData.setCleanStartTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
        fileData.setStatus(FileStatus.DATA_PROCESS);
        // todo save(fileData);
        // 然后从redis读取需要清洗的数据
        String dateStr = fileData.getDirName();//LocalDateTimeUtil.format(LocalDateTimeUtil.offset(t.getCreateDateTime(), -1, ChronoUnit.DAYS), "yyyyMMdd");
        String dataKey = "crm:file:" + fileData.getCategory() + ":" + dateStr;

        // 对于没有参与合并的表，没有参与合并的表
        if (fileData.getCategory().equals("null")) {
            // 没有参与合并的表
            fileData = new FileData();//// todo getOne(t.getId());
            fileData.setStatus(FileStatus.COMPLETED);

            fileData.setStartTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
            fileData.setEndTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
            fileData.setMsg("没有参与合并的表，不清洗");
            log.error("error");
            log.error("error");
            log.error("error");
            log.error("dont't find any data from  - " + fileData.getDirName() + "/" + fileData.getFileName());
            log.error("error");
            log.error("error");
            log.error("error");
            // todo save(fileData);
            // 这里同时删除redis缓存 给redis服务器的内存减小压力
            redisService.del(dataKey);
        } else {

            // 有记录 开始清洗
            if (!Objects.equals(redisService.lGetSize(dataKey), 0L)) {
                Long size = redisService.lGetSize(dataKey);
                log.error(fileData.getDirName() + "/" + fileData.getFileName() + "  get file data size 文件原有记录数：" + size);

                log.error(fileData.getDirName() + "/" + fileData.getFileName() + "  delete before data 删除原有的清洗记录（异常结束的）：" + size);
                // 删除原有的清洗记录（异常结束的）
                // todo deleteAllByFileData_Id(fileData.getId());
                fileData = new FileData();// todo getOne(fileData.getId());

                Long tempSize = size;
                Integer time = 0;

                //设定线程使用的资源类 dataIdMap
                fileData.setDataIdMap(new ConcurrentHashMap());
                while (tempSize > 0L) {
                    // 4万一次读取
                    Long readSize = 40000L;
                    Long start = time * readSize;
                    Long end = (time + 1) * readSize;
                    log.error("read and clean ： 读取并清洗第 " + fileData.getDirName() + "/" + fileData.getFileName() + " 的 " + +time + " 个4万数据");
                    if (end > size) {
                        end = size - 1;
                        tempSize = 0L;
                        time++;
                    } else {
                        tempSize -= readSize;
                        time++;
                    }
                    // 这里有redis宕机情况存在
                    // 这里不能全部清洗 会redis超时 + oom
                    List<String> string = new ArrayList<>();
                    do {
                        string = redisService.lGet(dataKey, start, end).stream().map(o -> o.toString()).collect(Collectors.toList());
                    } while (CollectionUtil.isEmpty(string));

                    // 清理
                    DataCleanHelper helper = new DataCleanHelper(redissonClient);
                    helper.handleList(string, threadCount, fileData, start.intValue());
                }

                log.error("clean end ： 文件清理完成");
                // 设置清洗完成
                fileData = // todo getOne(fileData.getId());
                        fileData.setStatus(FileStatus.DATA_COMPLETED);
                // todo save(fileData);
                // 这里同时删除redis缓存 给redis服务器的内存减小压力
                redisService.del(dataKey);

                System.out.println("..");
            } else {
                // 无记录
                fileData = // todo getOne(fileData.getId());
                        fileData.setStatus(FileStatus.COMPLETED);
                fileData.setStartTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                fileData.setEndTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
                fileData.setMsg("未从文件读取到任何数据");
                log.error("error");
                log.error("error");
                log.error("error");
                log.error("dont't find any data from  - " + fileData.getDirName() + "/" + fileData.getFileName());
                log.error("error");
                log.error("error");
                log.error("error");
                // todo save(fileData);
                // 这里同时删除redis缓存 给redis服务器的内存减小压力
                redisService.del(dataKey);
            }
        }

    }

}