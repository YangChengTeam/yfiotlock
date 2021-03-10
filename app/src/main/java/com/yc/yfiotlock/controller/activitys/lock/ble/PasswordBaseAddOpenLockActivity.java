package com.yc.yfiotlock.controller.activitys.lock.ble;

import com.clj.fastble.data.BleDevice;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

public abstract class PasswordBaseAddOpenLockActivity extends BaseBackActivity {
    protected BleDevice bleDevice;

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = LockIndexActivity.getInstance().getBleDevice();
    }
}
