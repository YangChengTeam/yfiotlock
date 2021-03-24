package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
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
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import rx.Subscriber;
import rx.functions.Action1;


public class Connect2Activity extends BaseAddActivity implements LockBLESend.NotifyCallback {

    @BindView(R.id.cpb_progress)
    CircularProgressBar mCpbProgress;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.ll_connecting)
    LinearLayout mLlConnecting;
    @BindView(R.id.tv_edit)
    TextView mTvEdit;
    @BindView(R.id.ll_connected)
    LinearLayout mLlConnected;

    private BleDevice bleDevice;
    private DeviceInfo deviceInfo;
    private LockBLESend lockBleSend;
    private DeviceEngin deviceEngin;

    private ValueAnimator valueAnimator;
    private ChangeDeviceNameDialog deviceNameDialog;

    private boolean isConnected = false;
    private boolean isOpOver = false;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
    }

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");

        deviceEngin = new DeviceEngin(this);
        lockBleSend = new LockBLESend(this, bleDevice);
        lockBleSend.setNotifyCallback(this);
        lockBleSend.registerNotify();


    }

    @Override
    protected void initViews() {
        super.initViews();
        backNavBar.setTitle(bleDevice.getName());
        initProcessAnimate();
        deviceNameDialog = new ChangeDeviceNameDialog(this);
        deviceNameDialog.setOnSureClick(name -> {
            cloudModifyDeivceName(name);
        });
        mTvEdit.setText(bleDevice.getName());
        bleBindWifi();
    }


    private void initProcessAnimate() {
        showConnectingUi();
        valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mCpbProgress.setProgress(value);
            mTvProgress.setText((int) value + "%");
            if (!isOpOver && value == 100 && !isConnected) {
                isOpOver = true;
                valueAnimator.end();
                onConnectFail();
            }
        });
        valueAnimator.setDuration(1000 * 65);
    }


    /**
     * 连接失败的跳转
     */
    private void onConnectFail() {
        Intent intent = new Intent(this, ConnectFailActivity.class);
        intent.putExtra("name", bleDevice.getName());
        startActivity(intent);
        finish();
    }

    /**
     * 连接中UI
     */
    private void showConnectingUi() {
        mLlConnected.setVisibility(View.GONE);
        mLlConnecting.setVisibility(View.VISIBLE);
    }

    /**
     * 连接成功UI
     */
    private void showConnectedUi() {
        mLlConnected.setVisibility(View.VISIBLE);
        mLlConnecting.setVisibility(View.GONE);
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mTvEdit, () -> {
            deviceNameDialog.show(mTvEdit.getText().toString());
        });
    }


    private void bleBindWifi() {
        if (lockBleSend != null) {
            String ssid = getIntent().getStringExtra("ssid");
            String pwd = getIntent().getStringExtra("pwd");
            valueAnimator.start();
            showConnectingUi();
            byte[] cmdBytes = LockBLESettingCmd.wiftDistributionNetwork(this, ssid, pwd);
            lockBleSend.send((byte) 0x01, (byte) 0x02, cmdBytes);
        }
    }


    private void cloudAddDevice() {
        bleSynctime();
        mLoadingDialog.show("添加设备中...");
        deviceEngin.addDeviceInfo(familyInfo.getId() + "", bleDevice.getName(), bleDevice.getMac(), aliDeviceName).subscribe(new Subscriber<ResultInfo<DeviceInfo>>() {
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

    private void cloudModifyDeivceName(String name) {
        if (deviceInfo != null && !TextUtils.isEmpty(deviceInfo.getId())) {
            mLoadingDialog.show("正在修改");
            deviceEngin.updateDeviceInfo(deviceInfo.getId(), name).subscribe(new Subscriber<ResultInfo<String>>() {
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
                        deviceInfo.setName(name);
                        EventBus.getDefault().post(deviceInfo);
                    } else {
                        msg = resultInfo != null && resultInfo.getMsg() != null ? resultInfo.getMsg() : msg;
                        ToastCompat.show(getContext(), msg);
                    }
                }
            });
        }
    }

    private String aliDeviceName = "000000000000";
    private void bleGetAliDeviceName() {
        if (lockBleSend != null) {
            byte[] cmdBytes = LockBLESettingCmd.getAlDeviceName(this);
            lockBleSend.send((byte) 0x01, (byte) 0x0A, cmdBytes, false);
        }
    }

    private void bleSynctime() {
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
        deviceInfo = (DeviceInfo) data;
        deviceInfo.setMacAddress(bleDevice.getMac());
        deviceInfo.setName(bleDevice.getName());
        deviceInfo.setDeviceId(aliDeviceName);
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

    private void nav2Index() {
        Intent intent = new Intent(this, LockIndexActivity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", deviceInfo);
        startActivity(intent);
        ConnectActivity.finish2();
        DeviceListActivity.finish2();
        ScanDeviceActivity.finish2();
    }


    @Override
    public void onNotifyReady(boolean isReady) {

    }

    private boolean isAdd;
    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x0A) {
            if(isAdd){
                return;
            }
            isAdd = true;
            aliDeviceName = LockBLEUtils.toHexString(lockBLEData.getOther()).replace(" ", "");
            LogUtil.msg("设备名称:" + aliDeviceName);
            cloudAddDevice();
        }
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x05) {
            LogUtil.msg("同步时间成功");
        } else {
            isConnected = true;
            valueAnimator.end();
            showConnectedUi();

            deviceInfo = new DeviceInfo();
            deviceInfo.setMacAddress(bleDevice.getMac());
            deviceInfo.setName(bleDevice.getName());
            deviceInfo.setDeviceId(aliDeviceName);
            bleGetAliDeviceName();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x02) {
            mLoadingDialog.dismiss();
            isOpOver = true;
            valueAnimator.end();
            onConnectFail();
        }
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
        if (isConnected) {
            nav2Index();
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
