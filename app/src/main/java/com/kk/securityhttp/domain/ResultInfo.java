package com.kk.securityhttp.domain;

/**
 * Created by zhangkai on 16/9/19.
 */

public class ResultInfo<T> {
    private int code;
    private String message;
    private T data;
    private transient boolean isCache;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }
}
