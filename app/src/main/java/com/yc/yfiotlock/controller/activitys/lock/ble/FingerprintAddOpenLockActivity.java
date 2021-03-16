package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FingerprintAddOpenLockActivity extends BaseBackActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_open_lock;
    }


    @Override
    protected void initViews() {
        super.initViews();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == (byte) 0x08 && bleData.getScmd() == (byte) 0x01 && bleData.getStatus() == (byte) 0x01) {
            Intent intent = new Intent(FingerprintAddOpenLockActivity.this, FingerprintAddNextOpenLockActivity.class);
            startActivity(intent);
        }
    }
}
