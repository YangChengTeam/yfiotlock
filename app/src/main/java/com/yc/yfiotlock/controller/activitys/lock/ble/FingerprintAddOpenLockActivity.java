package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.compat.ToastCompat;

public class FingerprintAddOpenLockActivity extends BaseFingerprintAddOpenLockActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_open_lock;
    }

    @Override
    protected void initViews() {
        super.initViews();
        bleAddFingerprint();

        VUiKit.postDelayed(12 * 1000, () -> {
            if (!lockBleSender.isOpOver()) {
                ToastCompat.show(getContext(), "操作失败");
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!lockBleSender.isOpOver()) {
            bleCancelDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void bleAddFingerprint() {
        byte[] bytes = LockBLEOpCmd.addFingerprint(lockInfo.getKey(), LockBLEManager.GROUP_TYPE, number);
        lockBleSender.send(mcmd, scmd, bytes);
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData != null && lockBLEData.getMcmd() == LockBLEEventCmd.MCMD && lockBLEData.getScmd() == LockBLEEventCmd.SCMD_FINGERPRINT_INPUT_COUNT && lockBLEData.getExtra()[0] == (byte) 0x01) {
            Intent intent = new Intent(getContext(), FingerprintAddNextOpenLockActivity.class);
            intent.putExtra("number", number);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        super.onNotifyFailure(lockBLEData);
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            ToastCompat.show(getContext(), "指纹添加失败");
            finish();
        }
    }


}
