package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.LogUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.dialogs.lock.ble.ChangeDeviceNameDialog;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import butterknife.BindView;
import rx.Subscriber;


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

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
        lockBleSend = new LockBLESend(this, bleDevice);
        lockBleSend.setNotifyCallback(this);
        deviceEngin = new DeviceEngin(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
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
    }

    private void initProcessAnimate() {
        showConnectingUi();
        valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mCpbProgress.setProgress(value);
            mTvProgress.setText((int) value + "%");
        });
        valueAnimator.setDuration(1000 * 60);
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
        mLoadingDialog.show("同步到云端");
        deviceEngin.addDeviceInfo(familyInfo.getId() + "", bleDevice.getName(), bleDevice.getMac(), aliDeviceName).subscribe(new Subscriber<ResultInfo<DeviceInfo>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
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
        if (deviceInfo != null && !TextUtils.isEmpty(deviceInfo.getDeviceId())) {
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
                    String msg = "服务器错误";
                    if (resultInfo != null && resultInfo.getCode() == 1) {
                        mTvEdit.setText(name);
                        deviceNameDialog.dismiss();
                        ToastCompat.show(getContext(), "修改成功");
                    } else {
                        msg = resultInfo != null && resultInfo.getMsg() != null ? resultInfo.getMsg() : msg;
                        ToastCompat.show(getContext(), msg);
                    }
                }
            });
        }

    }

    private String aliDeviceName = "YF-LOCK";

    private void bleGetAliDeviceName() {
        if (lockBleSend != null) {
            byte[] cmdBytes = LockBLESettingCmd.getAlDeviceName(this);
            lockBleSend.send((byte) 0x01, (byte) 0x0A, cmdBytes);
        }
    }

    @Override
    public void success(Object data) {
        deviceInfo = (DeviceInfo) data;
        deviceInfo.setName(bleDevice.getName());
        deviceInfo.setDeviceId(aliDeviceName);
    }

    private void nav2Index() {
        Intent intent = new Intent(this, LockIndexActivity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", deviceInfo);
        startActivity(intent);
        DeviceListActivity.finish2();
        ConnectActivity.finish2();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    @Override
    public void onNotifyReady() {
        bleBindWifi();
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x0A) {
            aliDeviceName = new String(lockBLEData.getOther());
        } else {
            valueAnimator.end();
            showConnectedUi();

            deviceInfo = new DeviceInfo();
            deviceInfo.setName(bleDevice.getName());
            deviceInfo.setDeviceId(aliDeviceName);
            cloudAddDevice();
            isConnected = true;
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x0A) {

        } else {
            valueAnimator.end();
            onConnectFail();
        }
    }
}
