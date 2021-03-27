package com.yc.yfiotlock.controller.activitys.lock.ble;


import android.app.Dialog;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.offline.OLTOfflineManager;
import com.yc.yfiotlock.utils.BleUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Random;

import rx.Subscriber;

public abstract class BaseAddOpenLockActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {
    protected OLTOfflineManager offlineManager;

    protected LockEngine lockEngine;
    protected DeviceInfo lockInfo;
    protected LockBLESend lockBleSend;
    protected LockBLESend cancelSend;
    protected byte mcmd;
    protected byte scmd;

    protected String number;
    protected String title;

    public void setTitle(String title) {
        this.title = title;
    }


    protected int type = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? 2 : 1;

    @Override
    protected void initVars() {
        super.initVars();
        offlineManager = OLTOfflineManager.getInstance(this);

        lockEngine = new LockEngine(this);
        lockInfo = getLockInfo();
        BleDevice bleDevice = getBleDevice();
        lockBleSend = new LockBLESend(this, bleDevice);
        Random rand = new Random();
        number = (10000000 + rand.nextInt(90000000)) + "";

        cancelSend = new LockBLESend(this, bleDevice);
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

    protected abstract void cloudAdd(int keyid);

    protected void cloudAdd(String name, int type, int keyid, String password) {
        OpenLockInfo openLockInfo = new OpenLockInfo();
        openLockInfo.setKeyid(keyid);
        openLockInfo.setName(name);
        openLockInfo.setType(type);
        openLockInfo.setLockId(lockInfo.getId());
        openLockInfo.setPassword(password);
        openLockInfo.setGroupType(LockBLEManager.GROUP_TYPE);
        offlineManager.saveOfflineData(BleUtil.getType(title) + lockInfo.getId() + "_add", openLockInfo);
        mLoadingDialog.show("添加中...");
        lockEngine.addOpenLockWay(lockInfo.getId(), name, keyid + "", type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                fail(name, type, keyid, password);
            }

            @Override
            public void onNext(ResultInfo<String> stringResultInfo) {
                if (stringResultInfo.getCode() == 1) {
                    offlineManager.delOfflineData(BleUtil.getType(title) + lockInfo.getId() + "_add", openLockInfo);
                    finish();
                    cloudAddSucc();
                    EventBus.getDefault().post(new OpenLockRefreshEvent());
                } else {
                    fail(name, type, keyid, password);
                }
            }
        });
    }

    public void fail(String name, int type, int keyid, String password) {
        if (retryCount-- > 0) {
            VUiKit.postDelayed(retryCount * (1000 - retryCount * 200), () -> {
                cloudAdd(name, type, keyid, password);
            });
        } else {
            retryCount = 3;
            GeneralDialog generalDialog = new GeneralDialog(getContext());
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("同步云端失败, 请重试");
            generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    cloudAdd(name, type, keyid, password);
                }
            });
            generalDialog.show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        lockBleSend.setNotifyCallback(this);
        lockBleSend.registerNotify();
        cancelSend.setNotifyCallback(this);
        cancelSend.registerNotify();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lockBleSend.setNotifyCallback(null);
        lockBleSend.unregisterNotify();
        cancelSend.setNotifyCallback(null);
        cancelSend.unregisterNotify();
    }

    protected void blecancelDialog() {
        GeneralDialog generalDialog = new GeneralDialog(getContext());
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("确认取消操作?");
        generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                mLoadingDialog.show("取消操作中...");
                blecancel();
            }
        });
        generalDialog.show();
    }


    private void blecancel() {
        if (cancelSend != null) {
            cancelSend.send((byte) 0x01, (byte) 0x07, LockBLESettingCmd.cancelOp(this), false);
        }
    }


    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            mLoadingDialog.dismiss();
            if (lockBLEData.getOther() != null) {
                String number = new String(Arrays.copyOfRange(lockBLEData.getOther(), 0, 8));
                if (number.equals(this.number)) {
                    int id = lockBLEData.getOther()[8];
                    cloudAdd(id );
                } else {
                    ToastCompat.show(getContext(), "流水号匹配不成功");
                }
            }
        } else if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x07) {
            lockBleSend.setOpOver(true);
            mLoadingDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x07) {
            lockBleSend.setOpOver(true);
            mLoadingDialog.dismiss();
            finish();
        }
    }
}
