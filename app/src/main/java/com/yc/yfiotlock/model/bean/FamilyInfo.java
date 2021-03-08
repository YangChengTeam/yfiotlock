package com.yc.yfiotlock.model.bean;

import java.io.Serializable;

public class FamilyInfo implements Serializable {
    private String name;
    private String location;
    private String homAddress;
    private boolean isDefault;
    private int deviceNum;
    private int id;

    public FamilyInfo() {
    }

    public FamilyInfo(String name, String location, String homAddress, boolean isDefault, int deviceNum, int id) {
        this.name = name;
        this.location = location;
        this.homAddress = homAddress;
        this.isDefault = isDefault;
        this.deviceNum = deviceNum;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHomAddress() {
        return homAddress;
    }

    public void setHomAddress(String homAddress) {
        this.homAddress = homAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(int deviceNum) {
        this.deviceNum = deviceNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
