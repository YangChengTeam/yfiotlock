package com.yc.yfiotlock.model.bean.lock;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

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
    private String regtime;
    private int battery;
    @JSONField(name = "device_id")
    private String deviceId;
    @JSONField(name = "is_online")
    private boolean isOnline;
    @JSONField(name = "mac_address")
    private String macAddress;
    private String model = "Locker";

    public DeviceInfo() {
    }

    public String getId() {
        if(TextUtils.isEmpty(id)){
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
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public int getBattery() {
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
}
