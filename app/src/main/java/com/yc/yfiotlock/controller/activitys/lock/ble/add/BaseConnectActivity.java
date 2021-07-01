package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.utils.BleUtil;
import com.yc.yfiotlock.utils.UserInfoCache;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseConnectActivity extends BaseAddActivity implements LockBLESender.NotifyCallback {

    protected boolean isDoDeviceAddAction = false;  // 是否 云端添加成功
    protected boolean isConnected = false;  // 是否 配网成功
    protected boolean isActiveDistributionNetwork = false;  // 是否 设备同步云端添加成功后 主动配网
    protected boolean isDeviceAdd = false;

    protected BleDevice bleDevice;
    protected DeviceInfo lockInfo;
    protected LockBLESender lockBleSender;
    protected DeviceEngin deviceEngin;
    protected DeviceDao deviceDao;

    protected String aliDeviceName = "000000000000";

    @Override
    protected void initVars() {
        super.initVars();
        deviceDao = App.getApp().getDb().deviceDao();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
        lockInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        isActiveDistributionNetwork = getIntent().getBooleanExtra("isActiveDistributionNetwork", false);
        deviceEngin = new DeviceEngin(this);
        if (lockInfo == null) {
            lockInfo = new DeviceInfo();
            lockInfo.setMacAddress(bleDevice.getMac());
            lockInfo.setName(bleDevice.getName());
        }
        lockBleSender = new LockBLESender(this, bleDevice, lockInfo.getKey());
    }

    // 本地添加设备
    @SuppressLint("CheckResult")
    protected void localDeviceAdd(DeviceInfo deviceInfo) {
        deviceDao.insertDeviceInfo(deviceInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onComplete() {
                mLoadingDialog.dismiss();
                UserInfoCache.incDeviceNumber();
                isDoDeviceAddAction = false;
                isDeviceAdd = true;
                nav2Index();
            }

            @Override
            public void onError(@NotNull Throwable e) {
                localDeviceAdd(deviceInfo);
            }
        });
    }

    private void localDeviceAdd() {
        lockInfo.setRegtime((int)(System.currentTimeMillis()/1000));
        lockInfo.setDeviceId(aliDeviceName);
        lockInfo.setFamilyId(familyInfo.getId());
        lockInfo.setMasterId(UserInfoCache.getUserInfo().getId());
        EventBus.getDefault().post(new IndexRefreshEvent());
        localDeviceAdd(lockInfo);
        EventBus.getDefault().post(new CloudDeviceAddEvent(lockInfo));
    }

    // 获取阿里名称
    protected void bleGetAliDeviceName() {
        if (lockBleSender != null) {
            if (!isActiveDistributionNetwork) {
                mLoadingDialog.show("添加设备中...");
            }
            byte[] cmdBytes = LockBLESettingCmd.getAliDeviceName(lockInfo.getKey());
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME, cmdBytes);
        }
    }

    // 设置key
    protected void bleSetkey(String oldKey, String newKey) {
        if (lockBleSender != null) {
            byte[] bytes = LockBLESettingCmd.setAesKey(oldKey, newKey);
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_SET_AES_KEY, bytes, false);
        }
    }

    protected void nav2Index() {
        Intent intent = new Intent(this, LockIndexActivity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", lockInfo);
        startActivity(intent);
        ConnectActivity.safeFinish();
        DeviceListActivity.safeFinish();
        ScanDeviceActivity.safeFinish();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(this);
            lockBleSender.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(null);
            lockBleSender.unregisterNotify();
        }
    }

    @Override
    public void onBackPressed() {
        if (isActiveDistributionNetwork) {
            finish();
            ConnectActivity.safeFinish();
        } else if (isDeviceAdd) {
            nav2Index();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME) {
            aliDeviceName = LockBLEUtil.toHexString(lockBLEData.getExtra()).replace(" ", "");
            LogUtil.msg("设备名称:" + aliDeviceName);
            lockInfo.setKey(LockBLEUtil.genKey());
            lockBleSender.setKey(lockInfo.getKey());
            bleSetkey(lockInfo.getOrigenKey(), lockInfo.getKey());
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_SET_AES_KEY) {
            if (isDoDeviceAddAction) {
                return;
            }
            isDoDeviceAddAction = true;
            localDeviceAdd();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME) {
            mLoadingDialog.dismiss();
            ToastCompat.show(getContext(), "添加失败,请重试");
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_SET_AES_KEY) {
            mLoadingDialog.dismiss();
            ToastCompat.show(getContext(), "添加失败,请重试");
        }
    }

    private boolean isNav2fail = false;
    protected void nav2fail() {
        if (isNav2fail) return;
        isNav2fail = true;
        Intent intent = new Intent(this, ConnectFailActivity.class);
        intent.putExtra("name", bleDevice.getName());
        startActivity(intent);
        finish();
    }
}
