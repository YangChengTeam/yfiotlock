package com.yc.yfiotlock.model.bean.lock.remote;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(tableName = "log_info", primaryKeys = {"id", "event_id"})
public class LogInfo {
    @NonNull
    private int id;

    @JSONField(name = "event_id")
    @ColumnInfo(name = "event_id")
    private int eventId;

    @JSONField(name = "log_type")
    @ColumnInfo(name = "log_type")
    private int logType;  // 日志 报警

    @JSONField(name = "pwd_type")
    @ColumnInfo(name = "type")
    private int type;  // 类型

    @JSONField(name = "group_type")
    @ColumnInfo(name = "group_type")
    private int groupType; // 设备权根

    @JSONField(name = "current_time")
    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "addtime")
    private long addtime;

    @JSONField(name = "locker_id")
    @ColumnInfo(name = "lock_id")
    private int lockId;

    @JSONField(name = "keyid")
    @ColumnInfo(name = "key_id")
    private int keyid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_add")
    private boolean isAdd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getKeyid() {
        return keyid;
    }

    public void setKeyid(int keyid) {
        this.keyid = keyid;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }


}
