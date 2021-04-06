package com.yc.yfiotlock.controller.activitys.lock.ble;


import android.app.Dialog;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Random;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

public abstract class BaseAddOpenLockActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {

    protected OpenLockDao openLockDao;
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


    protected int type = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? LockBLEManager.ALARM_TYPE : LockBLEManager.NORMAL_TYPE;

    @Override
    protected void initVars() {
        super.initVars();
        openLockDao = App.getApp().getDb().openLockDao();

        lockEngine = new LockEngine(this);
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
        BleDevice bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBleSend = new LockBLESend(this, bleDevice);
        Random rand = new Random();
        number = (10000000 + rand.nextInt(90000000)) + "";

        cancelSend = new LockBLESend(this, bleDevice);
    }


    protected abstract void localAddSucc();

    protected abstract void localAdd(int keyid);

    protected void localAdd(String name, int type, int keyid, String password) {
        OpenLockInfo openLockInfo = new OpenLockInfo();
        openLockInfo.setKeyid(keyid);
        openLockInfo.setName(name);
        openLockInfo.setType(type);
        openLockInfo.setLockId(lockInfo.getId());
        openLockInfo.setPassword(password);
        openLockInfo.setAddUserMobile("我");
        openLockInfo.setGroupType(LockBLEManager.GROUP_TYPE);
        openLockDao.insertOpenLockInfo(openLockInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                localAddSucc();
                if (CommonUtil.isNetworkAvailable(getContext())) {
                    cloudAdd(name, type, keyid, password);
                } else {
                    EventBus.getDefault().post(new OpenLockRefreshEvent());
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (retryCount-- > 3) {
                    localAdd(name, type, keyid, password);
                } else {
                    retryCount = 3;
                    cloudAdd(name, type, keyid, password);
                }
            }
        });
    }

    protected void cloudAdd(String name, int type, int keyid, String password) {
        mLoadingDialog.show("添加中...");
        lockEngine.addOpenLockWay(lockInfo.getId() + "", name, keyid + "", type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                fail();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    openLockDao.updateOpenLockInfo(lockInfo.getId(), keyid, true).subscribeOn(Schedulers.io()).subscribe();
                }
                fail();
            }
        });
    }

    @Override
    public void success(Object data) {
        super.success(data);
        EventBus.getDefault().post(new OpenLockRefreshEvent());
        finish();
    }

    @Override
    public void fail() {
        super.fail();
        EventBus.getDefault().post(new OpenLockRefreshEvent());
        finish();
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
            cancelSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_CANCEL_OP, LockBLESettingCmd.cancelOp(this), false);
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
                    localAdd(id);
                } else {
                    ToastCompat.show(getContext(), "流水号匹配不成功");
                }
            }
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CANCEL_OP) {
            lockBleSend.setOpOver(true);
            mLoadingDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CANCEL_OP) {
            lockBleSend.setOpOver(true);
            mLoadingDialog.dismiss();
            finish();
        }
    }
}
