package com.yc.yfiotlock.controller.activitys.lock.ble;

import androidx.annotation.Nullable;

import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.engin.LockEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Random;

import rx.Subscriber;

public abstract class BaseAddOpenLockActivity extends BaseBackActivity {
    protected LockEngine lockEngine;
    protected DeviceInfo lockInfo;
    protected LockBLESend lockBleSend;

    protected byte mcmd;
    protected byte scmd;

    protected String number;

    protected int type = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? 2 : 1;

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
        lockInfo = getLockInfo();
        BleDevice bleDevice = getBleDevice();
        lockBleSend = new LockBLESend(this, bleDevice);
        Random rand = new Random();
        number = rand.nextInt(100000000) + "";
    }

    protected DeviceInfo getLockInfo() {
        LockIndexActivity lockIndexActivity = LockIndexActivity.getInstance();
        if (lockIndexActivity != null) {
            return lockIndexActivity.getLockInfo();
        }
        return null;
    }

    protected BleDevice getBleDevice() {
        LockIndexActivity lockIndexActivity = LockIndexActivity.getInstance();
        if (lockIndexActivity != null) {
            return lockIndexActivity.getBleDevice();
        }
        return null;
    }

    protected abstract void cloudAddSucc();

    protected abstract void cloudAdd(String keyid);

    protected void cloudAdd(String name, int type, String keyid, String password) {
        mLoadingDialog.show("添加中...");
        lockEngine.addOpenLockWay(lockInfo.getId(), name, keyid, type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> stringResultInfo) {
                if (stringResultInfo.getCode() == 1) {
                    finish();
                    cloudAddSucc();
                    EventBus.getDefault().post(new OpenLockRefreshEvent());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        lockBleSend.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == mcmd && bleData.getScmd() == scmd) {
            if (bleData.getStatus() == (byte) 0x00 && bleData.getOther() != null) {
                String number = new String(Arrays.copyOfRange(bleData.getOther(), 0, 5));
                byte keyId = bleData.getOther()[6];
                // 验证流水号
                if (this.number.equals(number)) {
                    cloudAdd(keyId + "");
                }
            } else if (bleData.getStatus() == (byte) 0x01) {

            } else if (bleData.getStatus() == (byte) 0x10) {

            } else if (bleData.getStatus() == (byte) 0x11) {

            }
        }
    }

}
