package com.yc.yfiotlock.model.bean.lock.ble;

import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/18
 **/
public class LockInfo extends DeviceInfo {
    public LockInfo(String name) {
        this.setName(name);
    }

    private transient BleDevice bleDevice;

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}
