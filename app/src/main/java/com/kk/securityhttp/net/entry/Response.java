package com.kk.securityhttp.net.entry;

/**
 * Created by zhangkai on 16/8/18.
 */
public class Response {
    public int code;
    public String body;
    public okhttp3.Response response;

    public Response(String body) {
        this.body = body;
    }

    public Response(int code) {
        this.code = code;
    }

    public Response() {
    }
}
