package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

public class CloudAddEvent {
    private OpenLockInfo openLockInfo;

    public CloudAddEvent(OpenLockInfo openLockInfo){
        this.openLockInfo = openLockInfo;
    }

    public OpenLockInfo getOpenLockInfo() {
        return openLockInfo;
    }
}
