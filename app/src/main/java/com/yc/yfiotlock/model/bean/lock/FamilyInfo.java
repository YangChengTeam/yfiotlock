package com.yc.yfiotlock.model.bean.lock;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class FamilyInfo implements Serializable {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String address;
    @JSONField(name = "detail_address")
    private String detailAddress;
    @JSONField(name = "is_def")
    private boolean isDefault;
    private int num;

    public FamilyInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = !isDefault;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
