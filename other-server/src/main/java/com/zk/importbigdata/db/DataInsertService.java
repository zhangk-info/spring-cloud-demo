package com.zk.importbigdata.db;


import java.util.List;

public interface DataInsertService<T> {

    Integer saveOrUpdate(List<T> insertData, List<T> updateData, List<FileDataRecords> insertFileDataRecordss, List<FileDataRecords> updateFileDataRecordss);
}