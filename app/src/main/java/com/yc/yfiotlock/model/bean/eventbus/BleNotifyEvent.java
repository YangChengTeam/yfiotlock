package com.yc.yfiotlock.model.bean.eventbus;

public class BleNotifyEvent {
    private int status = 0;
    private String desp;
    public static int onNotifySuccess = 0;
    public static int onNotifyFailure = 1;
    public static int onNotifyChangeFailure = 2;

    public BleNotifyEvent(int status) {
        this.status = status;
    }

    public BleNotifyEvent(int status, String desp) {
        this.status = status;
        this.desp = desp;
    }

    public int getStatus() {
        return status;
    }

    public String getDesp() {
        return desp;
    }
}
