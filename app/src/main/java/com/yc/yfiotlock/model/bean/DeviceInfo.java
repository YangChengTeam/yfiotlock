package com.yc.yfiotlock.model.bean;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    private String id;
    private String itemName;
    private String value;

    public DeviceInfo() {
    }

    public DeviceInfo(String itemName, String value) {
        this.itemName = itemName;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
