package com.zk.importbigdata.msg;

public enum ImportStatus {
    READING("READING"),
    INSERTING("INSERTING"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS");

    public String code;

    private ImportStatus(String code) {
        this.code = code;
    }
}
