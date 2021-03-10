package com.yc.yfiotlock.model.bean.lock.ble;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class OpenLockInfo {
    @JSONField(name = "locker_id")
    private String lockerId;
    private String name;
    @JSONField(name = "keyid")
    private String keyId;
    @JSONField(name = "pwd_type")
    private String pwdType;
    @JSONField(name = "group_type")
    private String groupType;
    private String pwd;
    private String finger;
    private String nfc;
    private String model;

    public String getLockerId() {
        return lockerId;
    }

    public void setLockerId(String lockerId) {
        this.lockerId = lockerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getPwdType() {
        return pwdType;
    }

    public void setPwdType(String pwdType) {
        this.pwdType = pwdType;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public String getNfc() {
        return nfc;
    }

    public void setNfc(String nfc) {
        this.nfc = nfc;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
