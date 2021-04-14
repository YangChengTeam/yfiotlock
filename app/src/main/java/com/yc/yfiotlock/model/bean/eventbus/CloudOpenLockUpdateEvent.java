package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

public class CloudOpenLockUpdateEvent {
    private OpenLockInfo openLockInfo;

    public CloudOpenLockUpdateEvent(OpenLockInfo openLockInfo){
        this.openLockInfo = openLockInfo;
    }

    public OpenLockInfo getOpenLockInfo() {
        return openLockInfo;
    }
}
