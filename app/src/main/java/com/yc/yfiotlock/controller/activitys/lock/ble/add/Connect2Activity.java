package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @BindView(R.id.ll_connect_wifi)
    LinearLayout mLlConnectWifi;

    private ValueAnimator valueAnimator;
    private LockBLESend cancelSend;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
    }

    @Override
    protected void initVars() {
        super.initVars();
        cancelSend = new LockBLESend(this, bleDevice);
    }

    @Override
    protected void initViews() {
        super.initViews();
        backNavBar.setTitle(bleDevice.getName());
        initProcessAnimate();
        mTvEdit.setText(bleDevice.getName());
        bleBindWifi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cancelSend != null) {
            cancelSend.setNotifyCallback(this);
            cancelSend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cancelSend != null) {
            cancelSend.setNotifyCallback(null);
            cancelSend.unregisterNotify();
        }
    }

    private void initProcessAnimate() {
        showConnectingUi();
        valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mCpbProgress.setProgress(value);
            mTvProgress.setText((int) value + "%");
        });
        valueAnimator.setDuration(1000 * 65);
    }

    private void showConnectingUi() {
        mLlConnected.setVisibility(View.GONE);
        mLlConnecting.setVisibility(View.VISIBLE);
    }

    private void showConnectedUi() {
        if (isActiveDistributionNetwork) {
            mLlConnectWifi.setVisibility(View.VISIBLE);
            mLlConnected.setVisibility(View.GONE);
        } else {
            mLlConnected.setVisibility(View.VISIBLE);
            mLlConnectWifi.setVisibility(View.GONE);
        }
        mLlConnecting.setVisibility(View.GONE);
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mTvEdit, () -> {
            deviceNameDialog.show(mTvEdit.getText().toString());
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(DeviceInfo lockInfo) {
        mTvEdit.setText(lockInfo.getName());
    }


    private void bleBindWifi() {
        if (lockBleSend != null) {
            String ssid = getIntent().getStringExtra("ssid");
            String pwd = getIntent().getStringExtra("pwd");
            valueAnimator.start();
            showConnectingUi();
            byte[] cmdBytes = LockBLESettingCmd.wiftDistributionNetwork(this, ssid, pwd);
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_DISTRIBUTION_NETWORK, cmdBytes);
        }
    }


    @Override
    public void onBackPressed() {
        if (!lockBleSend.isOpOver()) {
            blecancelDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void blecancelDialog() {
        GeneralDialog generalDialog = new GeneralDialog(getContext());
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("确认取消操作?");
        generalDialog.setOnPositiveClickListener(dialog -> {
            mLoadingDialog.show("取消操作中...");
            blecancel();
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
        super.onNotifySuccess(lockBLEData);
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_DISTRIBUTION_NETWORK) {
            isConnected = true;
            valueAnimator.cancel();
            valueAnimator.end();
            showConnectedUi();
            ConnectActivity.safeFinish();
            bleGetAliDeviceName();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CANCEL_OP) {
            lockBleSend.setOpOver(true);
            mLoadingDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_DISTRIBUTION_NETWORK) {
            lockBleSend.setOpOver(true);
            mLoadingDialog.dismiss();
            valueAnimator.end();
            nav2fail();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CANCEL_OP) {
            lockBleSend.setOpOver(true);
            valueAnimator.end();
            valueAnimator.cancel();
            mLoadingDialog.dismiss();
            finish();
        }
    }
}
