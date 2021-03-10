package com.yc.yfiotlock.model.bean.lock.ble;

import java.io.Serializable;

public class BaseOpenLockInfo implements Serializable {
    private String id;
    private String name;
    private String model;
    private int keyid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getKeyid() {
        return keyid;
    }

    public void setKeyid(int keyid) {
        this.keyid = keyid;
    }
}
