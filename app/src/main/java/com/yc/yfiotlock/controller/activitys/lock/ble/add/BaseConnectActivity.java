package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtils;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.controller.dialogs.lock.ble.ChangeDeviceNameDialog;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.TimeInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;

import org.greenrobot.eventbus.EventBus;

import rx.Subscriber;
import rx.functions.Action1;

public abstract class BaseConnectActivity extends BaseAddActivity implements LockBLESend.NotifyCallback {

    protected boolean isOpOver = false;
    protected boolean isDeviceAdd = false;
    protected boolean isConnected = false;
    protected boolean isFromIndex = false;

    protected int isOnline = 0;

    protected BleDevice bleDevice;
    protected DeviceInfo lockInfo;
    protected LockBLESend lockBleSend;
    protected DeviceEngin deviceEngin;
    protected ChangeDeviceNameDialog deviceNameDialog;

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
        isFromIndex = getIntent().getBooleanExtra("isFromIndex", false);

        deviceEngin = new DeviceEngin(this);
        lockBleSend = new LockBLESend(this, bleDevice);
        lockBleSend.setNotifyCallback(this);
        lockBleSend.registerNotify();

        lockInfo = new DeviceInfo();
        lockInfo.setMacAddress(bleDevice.getMac());
        lockInfo.setName(bleDevice.getName());
    }

    @Override
    protected void initViews() {
        super.initViews();
        deviceNameDialog = new ChangeDeviceNameDialog(this);
        deviceNameDialog.setOnSureClick(this::cloudModifyDeivceName);
    }

    protected void cloudAddDevice() {
        bleSynctime();
        mLoadingDialog.show("添加设备中...");
        deviceEngin.addDeviceInfo(familyInfo.getId() + "", bleDevice.getName(), bleDevice.getMac(), aliDeviceName, isOnline).subscribe(new Subscriber<ResultInfo<DeviceInfo>>() {
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
            public void onNext(ResultInfo<DeviceInfo> resultInfo) {
                if (resultInfo != null && resultInfo.getCode() == 1) {
                    success(resultInfo.getData());
                } else {
                    fail();
                }
            }
        });
    }

    protected void cloudModifyDeivceName(String name) {
        if (lockInfo != null && !TextUtils.isEmpty(lockInfo.getId())) {
            mLoadingDialog.show("正在修改");
            deviceEngin.updateDeviceInfo(lockInfo.getId(), name).subscribe(new Subscriber<ResultInfo<String>>() {
                @Override
                public void onCompleted() {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onNext(ResultInfo<String> resultInfo) {
                    String msg = "更新出错";
                    if (resultInfo != null && resultInfo.getCode() == 1) {
                        deviceNameDialog.dismiss();
                        ToastCompat.show(getContext(), "修改成功");
                        lockInfo.setName(name);
                        EventBus.getDefault().post(new IndexRefreshEvent());
                    } else {
                        msg = resultInfo != null && resultInfo.getMsg() != null ? resultInfo.getMsg() : msg;
                        ToastCompat.show(getContext(), msg);
                    }
                }
            });
        }
    }

    protected String aliDeviceName = "000000000000";

    protected void bleGetAliDeviceName() {
        if (lockBleSend != null) {
            byte[] cmdBytes = LockBLESettingCmd.getAlDeviceName(this);
            lockBleSend.send((byte) 0x01, (byte) 0x0A, cmdBytes, true);
        }
    }

    protected void bleSynctime() {
        if (lockBleSend != null) {
            deviceEngin.getTime().subscribe(new Action1<ResultInfo<TimeInfo>>() {
                @Override
                public void call(ResultInfo<TimeInfo> info) {
                    if (info != null && info.getCode() == 1 && info.getData() != null) {
                        byte[] cmdBytes = LockBLESettingCmd.syncTime(getContext(), info.getData().getTime());
                        lockBleSend.send((byte) 0x01, (byte) 0x05, cmdBytes, true);
                    }
                }
            });
        }
    }

    @Override
    public void success(Object data) {
        lockInfo = (DeviceInfo) data;
        lockInfo.setMacAddress(bleDevice.getMac());
        lockInfo.setName(bleDevice.getName());
        lockInfo.setDeviceId(aliDeviceName);
        EventBus.getDefault().post(new IndexRefreshEvent());
    }

    @Override
    public void fail() {
        if (retryCount-- > 0) {
            VUiKit.postDelayed(retryCount * (1000 - retryCount * 200), () -> {
                cloudAddDevice();
            });
        } else {
            retryCount = 3;
            GeneralDialog generalDialog = new GeneralDialog(getContext());
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("同步云端失败, 请重试");
            generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    cloudAddDevice();
                }
            });
            generalDialog.show();
        }
    }

    protected void nav2Index() {
        Intent intent = new Intent(this, LockIndexActivity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", lockInfo);
        startActivity(intent);
        ConnectActivity.finish2();
        DeviceListActivity.finish2();
        ScanDeviceActivity.finish2();
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
        isOpOver = true;
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromIndex) {
            finish();
            ConnectActivity.finish2();
        } else if (isDeviceAdd) {
            nav2Index();
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x0A) {
            if (isDeviceAdd) {
                return;
            }
            isDeviceAdd = true;
            aliDeviceName = LockBLEUtils.toHexString(lockBLEData.getOther()).replace(" ", "");
            LogUtil.msg("设备名称:" + aliDeviceName);
            cloudAddDevice();
        }
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x05) {
            LogUtil.msg("同步时间成功");
        }
    }

    protected void nav2fail() {
        Intent intent = new Intent(this, ConnectFailActivity.class);
        intent.putExtra("name", bleDevice.getName());
        startActivity(intent);
        finish();
    }
}
