package com.zk.importbigdata.imports;

import com.zk.importbigdata.db.DataInsertService;
import com.zk.importbigdata.db.FileDataRecords;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DataMergeRunnable<T> implements Runnable {

    private final List<T> insertData;
    private final List<T> updateData;
    private final List<FileDataRecords> insertFileDataRecordss;
    private final List<FileDataRecords> updateFileDataRecordss;
    private final DataInsertService<T> dataInsertService;
    private boolean isAllowUpdate;
    private DataMergeService importService;

    public DataMergeRunnable(List<T> insertData, List<T> updateData, List<FileDataRecords> insertFileDataRecordss, List<FileDataRecords> updateFileDataRecordss, boolean isAllowUpdate, DataInsertService<T> dataInsertService, DataMergeService importService) {
        this.insertData = insertData;
        this.updateData = updateData;
        this.insertFileDataRecordss = insertFileDataRecordss;
        this.updateFileDataRecordss = updateFileDataRecordss;
        this.isAllowUpdate = isAllowUpdate;
        this.dataInsertService = dataInsertService;
        this.importService = importService;
    }

    @Override
    public void run() {
        try {
            log.error(Thread.currentThread().getName() + Thread.currentThread().getName() + "\t 办理业务" + "  insertSize = " + insertData.size() + "  inserDetaiSize = " + this.insertFileDataRecordss.size() + "  池活任务数：" + importService.runnableNum.get());
            this.dataInsertService.saveOrUpdate(this.insertData, isAllowUpdate ? this.updateData : null, this.insertFileDataRecordss, isAllowUpdate ? this.updateFileDataRecordss : null);
        } catch (Exception e) {
            // 注意，进入这里的异常不应该是insertData循环保存时候 [报错方法抛出] 的异常，单个异常自己处理，不然会中断insertData的循环
            log.error("dataInsertService.saveOrUpdate 一次执行出错 ！！");
            log.error(e.getMessage(), e);
        } finally {
            importService.runnableNum.decrementAndGet();
            log.error(Thread.currentThread().getName() + Thread.currentThread().getName() + "\t 办理业务成功" + "   池活任务数：" + importService.runnableNum.get());
        }
    }

}
