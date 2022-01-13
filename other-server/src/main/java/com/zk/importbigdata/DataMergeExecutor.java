package com.zk.importbigdata;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.commons.exception.ServiceException;
import com.zk.importbigdata.config.DataMergeMethod;
import com.zk.importbigdata.config.MergeConfig;
import com.zk.importbigdata.db.DataInsertService;
import com.zk.importbigdata.db.FileData;
import com.zk.importbigdata.db.FileDataRecords;
import com.zk.importbigdata.db.FileStatus;
import com.zk.importbigdata.imports.DataMergeService;
import com.zk.importbigdata.msg.IMsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据同步服务执行类
 * <p>
 * 必须：
 * 自定义
 * ImportConfig config
 * DataImportMethod method
 *
 * @param <T>
 */
@Slf4j
public class DataMergeExecutor<T> {

    private static Integer pageSize = 10000;

    /**
     * 执行入口
     *
     * @param config       配置
     * @param method       合并业务方法
     * @param restTemplate
     * @param objectMapper
     * @param threadCount  线程数
     */
    public void execute(MergeConfig config, DataMergeMethod method,
                        RestTemplate restTemplate, ObjectMapper objectMapper, Integer threadCount) {
        // 得到最早的一条待处理记录
        FileData fileData = null;
        try {
            // todo
            fileData = new FileData();
            fileData.setCategory("test");
            fileData.setId(1L);
            fileData.setFileName("test");
        } catch (Exception e) {
            log.error(" 得到最早的一条待处理记录 失败 原因： " + e.getMessage());
            log.error(e.getMessage(), e);
        }

        if (fileData != null) {
            log.info("得到最早一条类别为 " + fileData.getCategory() + " 的待处理记录，文件名为： " + fileData.getFileName());
            log.info("开始合并 " + fileData.getCategory() + " 数据：fileDataId -> {}", fileData.getId());
            LocalDateTime startDateTime = LocalDateTimeUtil.now();

            // 循环检查并分批插入
            Integer page = 0;

            // 更新：FileData 执行中 设置锁时间
            fileData.setStartTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));
            fileData.setStatus(FileStatus.IN_PROCESS);
            fileData.setLockTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));
            //todo saveFileData(fileData);
            log.error("更新FileData为执行中并设置开始时间和锁定时间");

            try {
                TimeUnit.MILLISECONDS.sleep(1000L);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            DataMergeService mergeService = getDataMergeService(fileData,
                    objectMapper, config, restTemplate, method, threadCount);

            while (true) {
                // 5万一页 5万一页 会获取超时  改成1万吧
                List<FileDataRecords> fileDataRecords = new ArrayList<FileDataRecords>();//todo findAllWaittingRecordsByFileDataId(fileData.getId(), page, pageSize);
                /***测试用***/
                for (int i = 0; i < 1000; i++) {
                    FileDataRecords records = new FileDataRecords();
                    records.setId(Long.valueOf(page * pageSize + i));
                    // {"String":"1|@|zhangk_0|@|511999199601200000"}
                    records.setData("{\"String\":\"" + (page * pageSize + i) + "|@|zhangk_" + i + "|@|51102119981010333X\"}");
                    fileDataRecords.add(records);
                }
                if (page == 10) {
                    fileDataRecords = null;
                }
                /***测试用***/

                if (CollectionUtils.isEmpty(fileDataRecords)) {
                    //跳出循环
                    break;
                }
                log.info("分页合并：  " + fileData.getFileName() + " 文件第 " + page + " 页有 " + fileDataRecords.size() + " 条数据需要合并 当前系统时间：" + LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));

                // 执行一页
                executeOnePage(mergeService, fileDataRecords, page);


                log.info("合并完成：   " + fileData.getFileName() + " 文件第 " + page + " 页有 " + fileDataRecords.size() + " 条数据合并完成 当前系统时间：" + LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));
                page++;
            }

            long costTime = LocalDateTimeUtil.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - startDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            log.info("合并完成 " + fileData.getCategory() + " 共 " + (page + 1) * pageSize + "条数据 " + " 耗时毫秒数 ： " + costTime + " 数据：fileDataId -> {}", fileData.getId());

            // 关闭线程池
            mergeService.closeThreadPool();

            // 更新：FileData
            fileData.setStatus(FileStatus.COMPLETED).setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));
            //todo saveFileData(fileData);


        } else {
            log.info("类别为 " + fileData.getCategory() + " 的记录未找到");
        }
        /**************** 结束处理合并 ****************/

    }

    private DataMergeService getDataMergeService(FileData fileData,
                                                 ObjectMapper objectMapper, MergeConfig config, RestTemplate restTemplate, DataMergeMethod method, Integer threadCount) {
        // 实现消息推送，这里存数据表或者发消息中间件
        IMsgSender msgSender = importMsg -> {
            // FileData保存消息
            // 更新：FileData
            try {
                fileData.setMsg(objectMapper.writeValueAsString(importMsg));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            fileData.setLockTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));
            log.error("msgSender 进行锁续费");
            // todo saveFileData(fileData);
        };

        // 新建DataImportService引用上面的config msgSender fileDataDetailList 和fileDataDetailRepository
        DataMergeService<T> readerService = new DataMergeService<>(config, msgSender, null, restTemplate, objectMapper, 0, threadCount);

        // 整表所有数据格式化
        readerService.setRowFormat(t -> method.format(t));

        readerService.registerDataInsert(new DataInsertService<T>() {

            // 保存数据
            @Override
            public Integer saveOrUpdate(List<T> insertData, List<T> updateData, List<FileDataRecords> insertDetails, List<FileDataRecords> updateDetails) {

                // crmDataProcessSdk 可能会执行出错 这时候不能影响程序正常运行 crmDataProcessSdk执行出错 method.saveFrom却执行成功，可能会影响程序正确性？
                // 插入的数据
                if (insertData != null && insertData.size() > 0) {

                    AtomicInteger i = new AtomicInteger(0);
                    insertData.forEach(t -> {
                        try {
                            // 如果是有单独的处理逻辑 请抓异常并单独处理FileDataRecords
                            FileDataRecords record = insertDetails.get(i.get());
                            // 调用merge数据的保存
                            method.saveFromRecords(t);
                            record.setReason(null);
                            record.setStatus(FileStatus.SUCCEED);
                            //todo saveRecord(record);
                        } catch (Exception e) {
                            // 自己消化，如果抛出去，整个insertData的循环都不会执行了
                            try {
                                FileDataRecords record = insertDetails.get(i.get());
                                if (e instanceof ServiceException) {
                                    ServiceException e2 = (ServiceException) e;
                                    record.setReason(e2.getMessage());
                                    // 由于其他服务下线造成的数据保存失败 需要重试！！ 所以改成待处理
                                    if (e2.getMessage().equals("API_CALL_ERROR") && e2.getMessage().contains("服务内部调用(GET)出错")) {
                                        record.setStatus(FileStatus.WAITTING);
                                    } else {
                                        record.setStatus(FileStatus.FAIL);
                                    }
                                } else {
                                    record.setReason(e.getMessage());
                                    record.setStatus(FileStatus.FAIL);
                                }

                                //todo saveRecord(record);
                            } catch (Exception e2) {
                                log.error("由于其他服务下线造成的数据保存失败 需要重试！！ 所以改成待处理 ");
                            }
                        } finally {
                            i.getAndIncrement();
                        }

                    });
                }
                // 一个子线程的一批数据执行完成之后进行锁续费 todo 改成一段时间自动续费节省资源
                // 锁续费
                fileData.setLockTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd hh:mm:ss"));
                // todo saveFileData(fileData);
                log.error("一个子线程的一批数据执行完成之后进行锁续费");
                // 更新的数据
                if (updateData != null && updateData.size() > 0) {
                }
                return 0;

            }
        });

        // 设置importId为当前文件的记录id并开启一个新线程执行合并
        readerService.setImportId(fileData.getId());
        return readerService;
    }

    /**
     * 执行一页
     */
    private void executeOnePage(DataMergeService mergeService, List<FileDataRecords> fileDataRecords, Integer page) {

        if (!CollectionUtils.isEmpty(fileDataRecords)) {
            // 设置开始行
            mergeService.setStartRow(page * pageSize);
            // 设置数据
            mergeService.setDetails(fileDataRecords);
            // 开始执行一页
            mergeService.start();
        }
    }


}
