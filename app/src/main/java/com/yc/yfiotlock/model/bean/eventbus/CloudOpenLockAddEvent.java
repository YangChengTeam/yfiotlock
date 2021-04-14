package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

public class CloudOpenLockAddEvent {
    private OpenLockInfo openLockInfo;

    public CloudOpenLockAddEvent(OpenLockInfo openLockInfo){
        this.openLockInfo = openLockInfo;
    }

    public OpenLockInfo getOpenLockInfo() {
        return openLockInfo;
    }
}
