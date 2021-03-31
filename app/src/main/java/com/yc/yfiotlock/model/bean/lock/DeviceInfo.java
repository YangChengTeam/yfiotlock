package com.yc.yfiotlock.model.bean.lock;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.yc.yfiotlock.model.bean.user.UserInfo;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    private String id;
    @JSONField(name = "locker_id")
    private String lockerId;
    private String name;
    @JSONField(name = "firmware_version")
    private String firmwareVersion;
    @JSONField(name = "protocol_version")
    private String protocolVersion;
    @JSONField(name = "add_time")
    private int regtime;
    private int battery;
    @JSONField(name = "device_id")
    private String deviceId;
    @JSONField(name = "is_online")
    private boolean isOnline;
    @JSONField(name = "mac_address")
    private String macAddress;
    private String model = "Locker";

    /**
     * 是否是共享的锁
     */
    @JSONField(name = "is_share")
    private int isShare;

    /**
     * 锁是否还有效 0失效 1 有效
     */
    private int has;

    private UserInfo user;

    public DeviceInfo() {
    }

    public String getId() {
        if (TextUtils.isEmpty(id)) {
            id = lockerId;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getFirmwareVersion() {
        if (TextUtils.isEmpty(firmwareVersion)) {
            firmwareVersion = "v1.0";
        }
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProtocolVersion() {
        if (TextUtils.isEmpty(protocolVersion)) {
            protocolVersion = "v1.1";
        }
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getRegtime() {
        return regtime;
    }

    public void setRegtime(int regtime) {
        this.regtime = regtime;
    }

    public int getBattery() {
        if (battery == 0) {
            battery = 3;
        }
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public int getIsShare() {
        return isShare;
    }

    public void setIsShare(int isShare) {
        this.isShare = isShare;
    }

    public int getHas() {
        return has;
    }

    public void setHas(int has) {
        this.has = has;
    }
}
