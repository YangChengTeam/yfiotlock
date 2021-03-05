package com.yc.yfiotlock.model.bean;

public class DeviceInfo {
    private String itemName;
    private String value;

    public DeviceInfo() {
    }

    public DeviceInfo(String itemName, String value) {
        this.itemName = itemName;
        this.value = value;
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
