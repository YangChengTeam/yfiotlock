package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

public class CloudOpenLockDeleteEvent {
    private OpenLockInfo openLockInfo;

    public CloudOpenLockDeleteEvent(OpenLockInfo openLockInfo){
        this.openLockInfo = openLockInfo;
    }

    public OpenLockInfo getOpenLockInfo() {
        return openLockInfo;
    }
}
