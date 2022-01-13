package com.zk.readbigdata.clean;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.zk.importbigdata.db.FileData;
import com.zk.importbigdata.db.FileDataRecords;
import com.zk.importbigdata.db.FileStatus;
import com.zk.readbigdata.constant.FileCategory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 合并客户基本信息
 *
 * @author zhangk
 */
@Slf4j
public class DataCleanHelper {

    private final RedissonClient redissonClient;

    public DataCleanHelper(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 多线程处理list
     * 先保存成等待中--
     * 等待所有线程存储数据完成后再改成处理中--
     * 避免定时器运行过程中，可能造成的，数据未存储完成便开始执行造成的部分数据未执行bug
     * <p>
     * 等待所有线程存储数据完成后再改成处理中--
     * CountDownLatch 计数器 每个线程执行完 计数器-1 直到所有子线程结束 主线程才继续向下执行
     *
     * @param data      数据list
     * @param threadNum 线程数
     */
    public void handleList(List<String> data, int threadNum, FileData fileData, int redisStartColumn) {
        int length = data.size();
        int tl = length % threadNum == 0 ? length / threadNum : (length
                / threadNum + 1);

        // 初始化countDown
        CountDownLatch threadSignal = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            int start = i * tl;
            int end = (i + 1) * tl;
            if (end > length) {
                end = length;
            }

            if (start > end) {
                // 跳出的时候也要让计数器减一
                threadSignal.countDown();
                continue;
            } else {
                HandleThread thread = new HandleThread("线程[" + (i + 1) + "] ", data, start, end > length ? length : end, fileData, threadSignal, redisStartColumn);
                thread.start();
            }
        }

        try {
            // 等待所有子线程都执行结束
            threadSignal.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void saveOneRecords(FileData fileData, FileDataRecords fileDataRecords) {

        // 加锁，同一时间只能有一个线程 处理这个dataId
        String lockKey = "crm:file_clean:" + fileData.getFileName() + ":" + fileDataRecords.getDataId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(3, TimeUnit.SECONDS);
        try {
            // 如果dataId已经写入了清理记录表，那么删除已写入的 重新写入以保留当前最新的
            if (fileData.dataIdMap.containsKey(fileDataRecords.getDataId())) {

                FileDataRecords before = new FileDataRecords();// todo getOne(Long.parseLong(fileData.dataIdMap.get(fileDataRecords.getDataId()).toString()));

                // 如果 之前存储的记录的行号 > 当前的行号 说明之前保存的是最新的
                if (before.getOrderNumber() > fileDataRecords.getOrderNumber()) {
                    log.error(" 之前存储的记录的行号 > 当前的行号 说明之前保存的是最新的 现在处理的这条不覆盖已存储的 -- 之前的:" + before.getOrderNumber() + " -- 现在的:" + fileDataRecords.getOrderNumber());
                    return;
                } else {
                    // todo deleteById(before.getId());
                    // todo save(fileDataRecords);
                    fileData.dataIdMap.put(fileDataRecords.getDataId(), fileDataRecords.getId());
                }
            } else {
                // todo save(fileDataRecords);
                fileData.dataIdMap.put(fileDataRecords.getDataId(), fileDataRecords.getId());
            }
        } finally {
            if (lock.isLocked()) { // 是否还是锁定状态
                if (lock.isHeldByCurrentThread()) { // 是当前执行线程的锁
                    lock.unlock(); // 释放锁
                }
            }
        }

        log.debug(fileData.getDirName() + " -- " + fileDataRecords.getOrderNumber());
    }

    class HandleThread extends Thread {

        private List<String> data;
        private int start;
        private int end;
        private FileData fileData;
        private CountDownLatch threadSignal;
        private int redisStartColumn;

        public HandleThread(String threadName, List<String> data, int start, int end, FileData fileData, CountDownLatch threadSignal, int redisStartColumn) {
            super.setName(threadName);
            this.data = data;
            this.start = start;
            this.end = end;
            this.fileData = fileData;
            this.threadSignal = threadSignal;
            this.redisStartColumn = redisStartColumn;
        }

        public void run() {
            try {
                // 处理数据
                List<String> string = data.subList(start, end);
                if (!CollectionUtils.isEmpty(string)) {

                    List<Map<String, String>> data = new ArrayList<>();

                    // 1.得到类别
                    FileCategory category = FileCategory.getByTableCode(fileData.getCategory());
                    String idIndex = category.getIdIndex();

                    // 构建数据
                    for (String info : string) {

                        // 对数据进行清洗 清洗 清洗 清洗
                        String dataId;
                        // 2.通过类别中定义的主键位置，得到主键
                        if (!category.getFileType().equals("excel")) {
                            dataId = info.split(category.getSeparator().replaceAll("\\|", "\\\\|"))[Integer.valueOf(idIndex)];
                        } else {
                            dataId = JSONUtil.parseObj(info).getStr(idIndex);
                        }

                        // 得到上一次清洗最后的记录
                        FileDataRecords lastFileDataRecords = new FileDataRecords();// getLastCleanDataByDataIdAndCategory(dataId, fileData.getCategory(), fileData.getId());

                        // 如果已经保存着有了，看数据是否有变更，没有的清洗出去
                        if (Objects.nonNull(lastFileDataRecords)) {
                            String oldData = lastFileDataRecords.getData();
                            String jsonData = "{\"String\":\"" + info + "\"}";
                            if (oldData.hashCode() != jsonData.hashCode() && !StringUtils.isEmpty(oldData)) {
                                Map<String, String> map = new HashMap<>();
                                map.put("String", info);
                                map.put("oldData", oldData);
                                map.put("dataId", dataId);
                                data.add(map);
                            } else {
                                // 数据的hashCode相等，认为数据没有变更，本次不处理
                            }
                        } else {
                            // 处理了重复的只取第一条 重复也取完
//                            tableDataRedcordsMap.put(id, "");
                            // 没保存着有 新增
                            Map<String, String> map = new HashMap<>();
                            map.put("String", info);
                            map.put("dataId", dataId);
                            data.add(map);
                        }
                    }

                    // 保存数据
                    this.savaNewData(data, fileData, start, redisStartColumn);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw e;
            } finally {
                threadSignal.countDown();
            }
        }


        /**
         * 保存合并数据
         */
        private void savaNewData(List<Map<String, String>> list, FileData fileData, Integer orderNum, Integer redisStartColumn) {
            // 倒序之后对数据过滤dataId重复 ，目的是为了只在这个list中保留最新的dataId的数据。
            Map temp = new HashMap();
            Collections.reverse(list); // 倒序排列
            list.stream().filter(t -> {
                String dataId = t.get("dataId");
                if (temp.containsKey(dataId)) {
                    return false;
                } else {
                    temp.put(dataId, null);
                    return true;
                }
            });
            for (Map<String, String> map : list) {

                // 转换成Json
                String jsonData = "{\"String\":\"" + map.get("String") + "\"}";
                String oldData = map.get("oldData");
                String dataId = map.get("dataId");
                FileDataRecords fileDataRecords = new FileDataRecords();
                fileDataRecords
                        .setData(jsonData)
                        .setDataId(dataId)
                        .setOldData(oldData)
                        .setOrderNumber(orderNum + redisStartColumn)
                        .setStatus(FileStatus.WAITTING)
                        .setDataId(fileData.getId().toString())
                        .setCategory(fileData.getCategory())
                        .setCreateDateTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));


                saveOneRecords(fileData, fileDataRecords);
                orderNum += 1;
            }

        }
    }

}
