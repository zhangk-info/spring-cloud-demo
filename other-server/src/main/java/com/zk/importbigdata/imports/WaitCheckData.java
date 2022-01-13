package com.zk.importbigdata.imports;


import com.zk.importbigdata.db.FileDataRecords;
import com.zk.importbigdata.msg.RowDataErrorMsg;

public class WaitCheckData<T> {

    private T data;
    private RowDataErrorMsg msg;
    private com.zk.importbigdata.db.FileDataRecords FileDataRecords;

    public WaitCheckData(T data, RowDataErrorMsg msg, FileDataRecords FileDataRecords) {
        this.data = data;
        this.msg = msg;
        this.FileDataRecords = FileDataRecords;
    }

    public FileDataRecords getFileDataRecords() {
        return FileDataRecords;
    }

    public void setFileDataRecords(FileDataRecords FileDataRecords) {
        this.FileDataRecords = FileDataRecords;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public RowDataErrorMsg getMsg() {
        return this.msg;
    }

    public void setMsg(RowDataErrorMsg msg) {
        this.msg = msg;
    }
}
