package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

public class CloudDeviceEditEvent {
    private DeviceInfo deviceInfo;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public CloudDeviceEditEvent(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
