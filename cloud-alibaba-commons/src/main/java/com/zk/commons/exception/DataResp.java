package com.zk.commons.exception;

public class DataResp<T>{
    private T data;
    private int code = 200;
    private String message = "success.";


    public DataResp(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public DataResp(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
