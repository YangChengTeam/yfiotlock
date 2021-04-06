package com.yc.yfiotlock.model.bean.lock.ble;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


@Entity(tableName = "open_lock_info")
public class OpenLockInfo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @Ignore
    private String model;
    @ColumnInfo(name = "key_id")
    private int keyid;
    @ColumnInfo(name = "type")
    private int type;  // 类型
    @ColumnInfo(name = "group_type")
    private int groupType; // 设备权根
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "lock_id")
    private int lockId;

    @ColumnInfo(name = "add_user_mobile")
    @JSONField(name = "add_user_mobile")
    private String addUserMobile;

    @ColumnInfo(name = "is_sync")
    private boolean isSync;

    public OpenLockInfo() {

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

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getAddUserMobile() {
        return addUserMobile;
    }

    public void setAddUserMobile(String addUserMobile) {
        this.addUserMobile = addUserMobile;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
