package com.yc.yfiotlock.model.bean.lock.ble;

import java.io.Serializable;

public class BaseOpenLockInfo implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
