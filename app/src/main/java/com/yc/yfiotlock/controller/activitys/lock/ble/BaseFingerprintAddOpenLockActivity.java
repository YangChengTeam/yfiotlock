package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FingerprintAddOpenLockActivity extends BaseAddOpenLockActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_open_lock;
    }


    @Override
    protected void initViews() {
        super.initViews();
        VUiKit.postDelayed(1000, () -> {
            bleAddFingerprint();
        });
    }

    private void bleAddFingerprint() {
        this.mcmd = (byte) 0x02;
        this.scmd = (byte) 0x08;
        byte[] bytes = LockBLEOpCmd.addFingerprint(this, LockBLEManager.GROUP_TYPE, number);
        lockBleSend.send(mcmd, scmd, bytes);
    }

    // fuck code
    @Override
    protected void cloudAddSucc() { }
    // fuck code
    @Override
    protected void cloudAdd(String keyid) { }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData != null && lockBLEData.getMcmd() == (byte) 0x08 && lockBLEData.getScmd() == (byte) 0x01 && lockBLEData.getStatus() == (byte) 0x01) {
            lockBleSend.setNotifyCallback(null);
            Intent intent = new Intent(getContext(), FingerprintAddNextOpenLockActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
