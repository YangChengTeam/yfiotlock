package com.yc.yfiotlock.model.bean.lock;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.annotation.JSONField;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import java.io.Serializable;

@Entity(tableName = "device_info", indices = {@Index(value = {"mac_address", "master_id"}, unique = true)})
public class DeviceInfo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    @JSONField(name = "firmware_version")
    private String firmwareVersion;
    @Ignore
    @JSONField(name = "protocol_version")
    private String protocolVersion;

    @ColumnInfo(name = "add_time")
    @JSONField(name = "add_time")
    private int regtime;

    @Ignore
    private int battery;

    @Ignore
    private int volume;

    @Ignore
    @JSONField(name = "device_id")
    private String deviceId;

    @Ignore
    @JSONField(name = "is_online")
    private boolean isOnline;

    @ColumnInfo(name = "mac_address")
    @JSONField(name = "mac_address")
    private String macAddress;

    @Ignore
    private String model = "Locker";

    @JSONField(name = "aes_key")
    @ColumnInfo(name = "key")
    private String key;

    // 是否是共享的锁
    @ColumnInfo(name = "is_share")
    @JSONField(name = "is_share")
    private boolean isShare;

    // 锁是否还有效 0失效 1 有效
    @Ignore
    @JSONField(name = "has")
    private boolean isValid;

    @ColumnInfo(name = "master_id")
    private int masterId;

    @Ignore
    private transient UserInfo user;

    @ColumnInfo(name = "is_add")
    private boolean isAdd;
    @ColumnInfo(name = "is_delete")
    private boolean isDelete;
    @ColumnInfo(name = "is_update")
    private boolean isUpdate;

    @ColumnInfo(name = "family_id")
    private int familyId;

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
        firmwareVersion = CacheUtil.getCache("firmwareVersion" + macAddress, String.class);
        if (TextUtils.isEmpty(firmwareVersion)) {
            firmwareVersion = "v1.0.0";
        }
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        CacheUtil.setCache("firmwareVersion" + macAddress, firmwareVersion);
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
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = "-";
        }
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

    public String getKey() {
        if (TextUtils.isEmpty(key)) {
            key = getOrigenKey();
        }
        return key;
    }

    public String getOrigenKey(){
        if(TextUtils.isEmpty(macAddress) || macAddress.length() < 8){
            return "00000000";
        }
        return CommonUtil.getOriginKey(macAddress);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
