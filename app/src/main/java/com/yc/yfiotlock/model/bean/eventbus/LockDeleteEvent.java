package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;

public class LockDeleteEvent {
    private DeviceInfo lockInfo;

    public LockDeleteEvent(DeviceInfo lockInfo) {
        this.lockInfo = lockInfo;
    }

    public DeviceInfo getLockInfo() {
        return lockInfo;
    }
}

