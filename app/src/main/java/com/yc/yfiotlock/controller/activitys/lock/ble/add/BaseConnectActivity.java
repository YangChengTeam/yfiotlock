package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtils;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.TimeInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.utils.UserInfoCache;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public abstract class BaseConnectActivity extends BaseAddActivity implements LockBLESend.NotifyCallback {

    protected boolean isDeviceAdd = false;  // 是否 设备同步云端添加成功
    protected boolean isConnected = false;  // 是否 配网成功
    protected boolean isActiveDistributionNetwork = false;  // 是否 设备同步云端添加成功后 主动配网


    protected BleDevice bleDevice;
    protected DeviceInfo lockInfo;
    protected LockBLESend lockBleSend;
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
        lockBleSend = new LockBLESend(this, bleDevice, lockInfo.getKey());
    }

    @SuppressLint("CheckResult")
    protected void localDeviceAdd() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setFamilyId(familyInfo.getId());
        deviceInfo.setName(bleDevice.getName());
        deviceInfo.setMacAddress(bleDevice.getMac());
        deviceInfo.setAdd(false);
        deviceInfo.setDeviceId(aliDeviceName);
        deviceDao.insertDeviceInfo(deviceInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                EventBus.getDefault().post(new CloudDeviceAddEvent(deviceInfo));
                EventBus.getDefault().post(new IndexRefreshEvent());
                UserInfoCache.incDeviceNumber();
                nav2Index();
            }
        });
    }

    protected void bleGetAliDeviceName() {
        if (lockBleSend != null) {
            byte[] cmdBytes = LockBLESettingCmd.getAliDeviceName(lockInfo.getKey());
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME, cmdBytes, true);
        }
    }


    protected void nav2Index() {
        Intent intent = new Intent(this, LockIndexActivity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", lockInfo);
        startActivity(intent);
        finish();
        ConnectActivity.safeFinish();
        DeviceListActivity.safeFinish();
        ScanDeviceActivity.safeFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(this);
            lockBleSend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
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
            aliDeviceName = LockBLEUtils.toHexString(lockBLEData.getExtra()).replace(" ", "");
            LogUtil.msg("设备名称:" + aliDeviceName);
            if (isDeviceAdd || isActiveDistributionNetwork) {
                return;
            }
            isDeviceAdd = true;
            localDeviceAdd();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME) {
            if (isDeviceAdd || isActiveDistributionNetwork) {
                return;
            }
            isDeviceAdd = true;
            localDeviceAdd();
        }
    }

    protected void nav2fail() {
        Intent intent = new Intent(this, ConnectFailActivity.class);
        intent.putExtra("name", bleDevice.getName());
        startActivity(intent);
        finish();
    }
}
