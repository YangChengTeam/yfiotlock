package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

public class CloudDeviceDelEvent {
    private DeviceInfo deviceInfo;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public CloudDeviceDelEvent(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
