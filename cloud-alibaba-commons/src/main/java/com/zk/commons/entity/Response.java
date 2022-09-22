package com.zk.commons.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("响应信息主体")
public class Response<T> implements Serializable {
    private static final long serialVersionUID = -4105300051120165695L;
    private static String OK = "ok";
    @ApiModelProperty("返回消息对象")
    private Response.Meta meta;
    @ApiModelProperty("数据")
    private T data;
    @ApiModelProperty("主键id")
    private String id;

    public Response() {
    }

    public Response(final Response.Meta meta, final T data, final String id) {
        this.meta = meta;
        this.data = data;
        this.id = id;
    }

    public static <T> Response<T> success() {
        Response.Meta meta = new Response.Meta(true, OK);
        return restResult(meta, null, null);
    }

    public static <T> Response<T> successWithMsg(String message) {
        Response.Meta meta = new Response.Meta(true, message);
        return restResult(meta, null, null);
    }

    public static <T> Response<T> successWithMsg(String message, String id) {
        Response.Meta meta = new Response.Meta(true, message);
        return restResult(meta, null, id);
    }

    public static <T> Response<T> successWithData(T data) {
        Response.Meta meta = new Response.Meta(true, OK);
        return restResult(meta, data, null);
    }

    public static <T> Response<T> successWithData(T data, String message) {
        Response.Meta meta = new Response.Meta(true, message);
        return restResult(meta, data, null);
    }

    public static <T> Response<T> successWithData(T data, String message, String id) {
        Response.Meta meta = new Response.Meta(true, message);
        return restResult(meta, data, id);
    }

    public static <T> Response<T> failureWithMsg(T data, String message) {
        Response.Meta meta = new Response.Meta(false, message);
        return restResult(meta, data, (String) null);
    }

    public static <T> Response<T> failureWithMsg(T data, String message, int code) {
        Response.Meta meta = new Response.Meta(false, message, code);
        return restResult(meta, data, (String) null);
    }

    public static <T> Response<T> failureWithMsg(String message) {
        Response.Meta meta = new Response.Meta(false, message);
        return restResult(meta, null, null);
    }

    public static <T> Response<T> failureWithDetailMsg(String message, Object detail) {
        Response.Meta meta = new Response.Meta(false, message, detail);
        return restResult(meta, null, null);
    }

    public static <T> Response<T> failureWithMsg(String message, int code) {
        Response.Meta meta = new Response.Meta(false, message, code);
        return restResult(meta, null, null);
    }

    private static <T> Response<T> restResult(Response.Meta meta, T data, String id) {
        Response<T> apiResult = new Response();
        apiResult.setMeta(meta);
        apiResult.setData(data);
        apiResult.setId(id);
        return apiResult;
    }

    public String toString() {
        return "Response(meta=" + this.getMeta() + ", data=" + this.getData() + ", id=" + this.getId() + ")";
    }

    public Response.Meta getMeta() {
        return this.meta;
    }

    public Response<T> setMeta(final Response.Meta meta) {
        this.meta = meta;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public Response<T> setData(final T data) {
        this.data = data;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public Response<T> setId(final String id) {
        this.id = id;
        return this;
    }

    public static class Meta implements Serializable {
        private static final long serialVersionUID = 5178675809807873277L;
        @ApiModelProperty(
                value = "是否成功",
                notes = "true or false "
        )
        private boolean success;
        @ApiModelProperty(
                value = "返回消息",
                notes = "对应的消息信息"
        )
        private String message;
        @ApiModelProperty(
                value = "状态码",
                notes = "见类ResponseStatusCodeConstants"
        )
        private int code = 0;
        @ApiModelProperty(
                value = "返回消息详情",
                notes = "对应的消息详情"
        )
        private Object detail;

        public Meta(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public Meta(boolean success, String message, int code) {
            this.success = success;
            this.message = message;
            this.code = code;
        }

        public Meta(boolean success, String message, Object detail) {
            this.success = success;
            this.message = message;
            this.detail = detail;
        }

        public Meta() {
        }

        public boolean isSuccess() {
            return this.success;
        }

        public Response.Meta setSuccess(final boolean success) {
            this.success = success;
            return this;
        }

        public String getMessage() {
            return this.message;
        }

        public Response.Meta setMessage(final String message) {
            this.message = message;
            return this;
        }

        public int getCode() {
            return this.code;
        }

        public Response.Meta setCode(final int code) {
            this.code = code;
            return this;
        }

        public Object getDetail() {
            return this.detail;
        }

        public Response.Meta setDetail(final Object detail) {
            this.detail = detail;
            return this;
        }
    }
}
