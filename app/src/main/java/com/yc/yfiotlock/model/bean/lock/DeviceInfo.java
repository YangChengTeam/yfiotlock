package com.yc.yfiotlock.model.bean.lock;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    private String id;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
