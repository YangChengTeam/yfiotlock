package com.yc.yfiotlock.libs.fastble.callback;

import com.yc.yfiotlock.libs.fastble.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
