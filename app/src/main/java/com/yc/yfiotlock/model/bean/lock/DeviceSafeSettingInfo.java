package com.yc.yfiotlock.model.bean.lock;

public class DeviceSafeSettingInfo {

    private final static int PASSWORD_TYPE = 1;
    private final static int FINGERPRINT_TYPE = 2;

    private int type;
    private String password;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
