package com.yc.yfiotlock.libs.fastble.callback;


import com.yc.yfiotlock.libs.fastble.data.BleDevice;

import java.util.List;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }
}
