package com.zk.importbigdata.msg;


import com.zk.importbigdata.config.Header;

public class ColDataErrorMsg {

    private Integer colIndex;
    private String header;
    private String value;
    private String msg;
    private String msgTip;

    public ColDataErrorMsg(Integer colIndex, Header header, String value, String msg) {
        this.colIndex = colIndex;
        this.value = value;
        this.header = header.getName();
        this.msg = msg;
    }

    public ColDataErrorMsg(Integer colIndex, Header header, String value, String msg, String msgTip) {
        this.colIndex = colIndex;
        this.value = value;
        this.header = header.getName();
        this.msg = msg;
        this.msgTip = msgTip;
    }

    public Integer getColIndex() {
        return this.colIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgTip() {
        return this.msgTip;
    }

    public void setMsgTip(String msgTip) {
        this.msgTip = msgTip;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
