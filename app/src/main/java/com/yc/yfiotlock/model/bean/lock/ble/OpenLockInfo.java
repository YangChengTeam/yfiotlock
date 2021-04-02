package com.yc.yfiotlock.model.bean.lock.ble;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class OpenLockInfo implements Serializable {
    private String id;
    private String name;
    private String model;
    private int keyid;
    private int type;
    private int groupType;
    private String password;
    private String lockId;
    @JSONField(name = "add_user_mobile")
    private String addUserMobile;

    public OpenLockInfo() {

    }

    public String getId() {
        if (id == null) {
            id = "";
        }
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getAddUserMobile() {
        return addUserMobile;
    }

    public void setAddUserMobile(String addUserMobile) {
        this.addUserMobile = addUserMobile;
    }
}
