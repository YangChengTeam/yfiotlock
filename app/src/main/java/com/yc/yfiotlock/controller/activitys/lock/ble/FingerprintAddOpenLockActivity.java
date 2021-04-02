package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
            if (!lockBleSend.isOpOver()) {
                ToastCompat.show(getContext(), "操作失败");
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!lockBleSend.isOpOver()) {
            blecancelDialog();
        }
    }

    private void bleAddFingerprint() {
        byte[] bytes = LockBLEOpCmd.addFingerprint(this, LockBLEManager.GROUP_TYPE, number);
        lockBleSend.send(mcmd, scmd, bytes);
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData != null && lockBLEData.getMcmd() == LockBLEEventCmd.MCMD && lockBLEData.getScmd() == LockBLEEventCmd.SCMD_INPUT_PRINTFINGER && lockBLEData.getStatus() == (byte) 0x01) {
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
            ToastCompat.show(getContext(), "操作失败");
            finish();
        }
    }


}
