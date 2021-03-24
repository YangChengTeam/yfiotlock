package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kk.securityhttp.utils.LogUtil;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtils;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import butterknife.BindView;


public class Connect2Activity extends BaseConnectActivity {

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

    private ValueAnimator valueAnimator;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
    }

    @Override
    protected void initVars() {
        super.initVars();
        isOnline = 1;
    }

    @Override
    protected void initViews() {
        super.initViews();
        backNavBar.setTitle(bleDevice.getName());
        initProcessAnimate();
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
                nav2fail();
            }
        });
        valueAnimator.setDuration(1000 * 65);
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

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        super.onNotifySuccess(lockBLEData);
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x02) {
            isConnected = true;
            valueAnimator.end();
            showConnectedUi();
            bleGetAliDeviceName();

            LockBLEManager.setBindWifi(bleDevice.getMac());
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        super.onNotifySuccess(lockBLEData);
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x02) {
            mLoadingDialog.dismiss();
            isOpOver = true;
            valueAnimator.end();
            nav2fail();
        }
    }


}
