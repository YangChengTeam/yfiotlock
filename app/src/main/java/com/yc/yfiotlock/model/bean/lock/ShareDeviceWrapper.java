package com.yc.yfiotlock.model.bean.lock;

import com.alibaba.fastjson.annotation.JSONField;
import com.yc.yfiotlock.model.bean.user.UserInfo;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/31
 **/
public class ShareDeviceWrapper {
    private String id;
    private String name;
    @JSONField(name = "share_uid")
    private String shareUid;
    @JSONField(name = "receive_uid")
    private String receiveUid;
    @JSONField(name = "share_time")
    private String shareTime;
    @JSONField(name = "share_status")
    private int shareStatus;
    @JSONField(name = "receive_status")
    private int receiveStatus;
    @JSONField(name = "locker_id")
    private String lockerId;
    private String model;
    @JSONField(name = "receive_user")
    private UserInfo receiveUser;
    @JSONField(name = "share_user")
    private UserInfo shareUser;
    private DeviceInfo locker;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShareUid() {
        return shareUid;
    }

    public void setShareUid(String shareUid) {
        this.shareUid = shareUid;
    }

    public String getReceiveUid() {
        return receiveUid;
    }

    public void setReceiveUid(String receiveUid) {
        this.receiveUid = receiveUid;
    }

    public String getShareTime() {
        return shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public int getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(int shareStatus) {
        this.shareStatus = shareStatus;
    }

    public int getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(int receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public String getLockerId() {
        return lockerId;
    }

    public void setLockerId(String lockerId) {
        this.lockerId = lockerId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UserInfo getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(UserInfo receiveUser) {
        this.receiveUser = receiveUser;
    }

    public UserInfo getShareUser() {
        return shareUser;
    }

    public void setShareUser(UserInfo shareUser) {
        this.shareUser = shareUser;
    }

    public DeviceInfo getLocker() {
        return locker;
    }

    public void setLocker(DeviceInfo locker) {
        this.locker = locker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
