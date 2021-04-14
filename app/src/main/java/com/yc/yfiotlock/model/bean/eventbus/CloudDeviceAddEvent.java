package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

public class CloudDeviceAddEvent {
    private DeviceInfo deviceInfo;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public CloudDeviceAddEvent(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
