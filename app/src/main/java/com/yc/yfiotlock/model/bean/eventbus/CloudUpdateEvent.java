package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

public class CloudUpdateEvent {
    private OpenLockInfo openLockInfo;

    public CloudUpdateEvent(OpenLockInfo openLockInfo){
        this.openLockInfo = openLockInfo;
    }

    public OpenLockInfo getOpenLockInfo() {
        return openLockInfo;
    }
}
