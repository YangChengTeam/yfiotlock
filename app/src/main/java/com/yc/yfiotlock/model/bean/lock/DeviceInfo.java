package com.yc.yfiotlock.model.bean.lock;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.utils.CacheUtil;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    private int id;
    private String name;
    @JSONField(name = "firmware_version")
    private String firmwareVersion;
    @JSONField(name = "protocol_version")
    private String protocolVersion;
    @JSONField(name = "add_time")
    private int regtime;
    private int battery;
    private int volume;
    @JSONField(name = "device_id")
    private String deviceId;
    @JSONField(name = "is_online")
    private boolean isOnline;
    @JSONField(name = "mac_address")
    private String macAddress;
    private String model = "Locker";

    // 是否是共享的锁
    @JSONField(name = "is_share")
    private boolean isShare;

    // 锁是否还有效 0失效 1 有效
    @JSONField(name = "has")
    private boolean isValid;

    private transient UserInfo user;

    public DeviceInfo() {
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

    public String getFirmwareVersion() {
        firmwareVersion = CacheUtil.getCache("firmwareVersion", String.class);
        if (TextUtils.isEmpty(firmwareVersion)) {
            firmwareVersion = "v1.0.0";
        }
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProtocolVersion() {
        if (TextUtils.isEmpty(protocolVersion)) {
            protocolVersion = "v1.5.0";
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
        battery = MMKV.defaultMMKV().getInt("battery", -1);
        if (battery == -1) {
            battery = 100;
        }
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getVolume() {
        if (volume == 0) {
            volume = 3;
        }
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
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

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean isShare) {
        this.isShare = isShare;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}
