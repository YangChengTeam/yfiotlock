package com.yc.yfiotlock.model.bean.lock.remote;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/4/1
 **/
public class NetworkStateInfo {
    public static final String ONLINE = "ONLINE";
    public static final String OFFLINE = "OFFLINE";
    private int status;
    private String msg;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
