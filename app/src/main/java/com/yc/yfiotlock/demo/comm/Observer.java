package com.yc.yfiotlock.demo.comm;


import com.yc.yfiotlock.libs.fastble.data.BleDevice;

public interface Observer {
    void disConnected(BleDevice bleDevice);
}
