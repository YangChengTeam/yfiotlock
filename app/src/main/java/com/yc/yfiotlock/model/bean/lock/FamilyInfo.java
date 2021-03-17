package com.yc.yfiotlock.model.bean.lock;

import java.io.Serializable;

public class FamilyInfo implements Serializable {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String address;
    private String detail_address;
    private int is_def = 1;
    private int num;

    private boolean isUpdateList;

    public boolean isUpdateList() {
        return isUpdateList;
    }

    public void setUpdateList(boolean updateList) {
        isUpdateList = updateList;
    }

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

    public String getDetail_address() {
        return detail_address;
    }

    public void setDetail_address(String detail_address) {
        this.detail_address = detail_address;
    }

    public int isIs_def() {
        return is_def;
    }

    public void setIs_def(int is_def) {
        this.is_def = is_def;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
