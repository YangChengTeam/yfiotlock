package com.yc.yfiotlock.model.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 */
public class HomeInfo {
    private int id;
    private String name;
    private String model;
    @JSONField(name = "is_def")
    private int isDef;
    private String longitude;
    private String latitude;
    private String address;
    @JSONField(name = "detail_address")
    private String detailAddress;

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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getIsDef() {
        return isDef;
    }

    public void setIsDef(int isDef) {
        this.isDef = isDef;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
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
}
