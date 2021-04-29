package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.utils.UserInfoCache;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

public abstract class BaseConnectActivity extends BaseAddActivity implements LockBLESender.NotifyCallback {

    protected boolean isDeviceAdd = false;  // 是否 设备同步云端添加成功
    protected boolean isConnected = false;  // 是否 配网成功
    protected boolean isActiveDistributionNetwork = false;  // 是否 设备同步云端添加成功后 主动配网


    protected BleDevice bleDevice;
    protected DeviceInfo lockInfo;
    protected LockBLESender lockBleSender;
    protected DeviceEngin deviceEngin;
    protected DeviceDao deviceDao;

    protected String aliDeviceName = "000000000000";
    private int retryCount = 3;

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

    @SuppressLint("CheckResult")
    protected void localDeviceAdd(DeviceInfo deviceInfo) {
        deviceDao.insertDeviceInfo(deviceInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                EventBus.getDefault().post(new IndexRefreshEvent());
                UserInfoCache.incDeviceNumber();
                nav2Index();
            }
        });
    }

    protected void cloudDeviceAdd() {
        deviceEngin.addDeviceInfo(familyInfo.getId() + "", bleDevice.getName(), bleDevice.getMac(), aliDeviceName, lockInfo.getKey()).subscribe(new Subscriber<ResultInfo<DeviceInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                fail();
            }

            @Override
            public void onNext(ResultInfo<DeviceInfo> resultInfo) {
                if (resultInfo != null && (resultInfo.getCode() == 1)) {
                    mLoadingDialog.dismiss();
                    success(resultInfo.getData());
                } else {
                    fail();
                }
            }
        });
    }

    @Override
    public void success(Object data) {
        lockInfo = (DeviceInfo) data;
        lockInfo.setMacAddress(bleDevice.getMac());
        lockInfo.setName(bleDevice.getName());
        lockInfo.setDeviceId(aliDeviceName);
        lockInfo.setFamilyId(familyInfo.getId());
        lockInfo.setAdd(true);
        lockInfo.setKey(LockBLEUtil.genKey());
        lockInfo.setMasterId(UserInfoCache.getUserInfo().getId());
        localDeviceAdd(lockInfo);
    }

    @Override
    public void fail() {
        if (retryCount-- > 0) {
            VUiKit.postDelayed(retryCount * (1000 - retryCount * 200), this::cloudDeviceAdd);
        } else {
            retryCount = 3;
            mLoadingDialog.dismiss();
            GeneralDialog generalDialog = new GeneralDialog(getContext());
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("同步云端失败, 请重试");
            generalDialog.setOnPositiveClickListener(dialog -> {
                mLoadingDialog.show("添加设备中...");
                cloudDeviceAdd();
            });
            generalDialog.show();
        }
    }

    protected void bleGetAliDeviceName() {
        if (lockBleSender != null) {
            if (!isActiveDistributionNetwork) {
                mLoadingDialog.show("添加设备中...");
            }
            byte[] cmdBytes = LockBLESettingCmd.getAliDeviceName(lockInfo.getKey());
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME, cmdBytes);
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
            if (isDeviceAdd || isActiveDistributionNetwork) {
                return;
            }
            isDeviceAdd = true;
            cloudDeviceAdd();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_ALIDEVICE_NAME) {
            if (isDeviceAdd || isActiveDistributionNetwork) {
                return;
            }
            isDeviceAdd = true;
            cloudDeviceAdd();
        }
    }

    protected void nav2fail() {
        Intent intent = new Intent(this, ConnectFailActivity.class);
        intent.putExtra("name", bleDevice.getName());
        startActivity(intent);
        finish();
    }
}
