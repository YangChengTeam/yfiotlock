package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.lock.ble.ChangeDeviceNameDialog;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Connect2Activity extends BaseBackActivity {

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

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
    }

    public static void start(Context context, BleDevice bleDevice) {
        Intent intent = new Intent(context, Connect2Activity.class);
        intent.putExtra("bleDevice", bleDevice);
        context.startActivity(intent);
    }

    private String deviceName = "";

    @Override
    protected void initViews() {
        super.initViews();
        BleDevice bleDevice = getIntent().getParcelableExtra("bleDevice");
        if (bleDevice != null) {
            deviceName = bleDevice.getName();
        }
        backNavBar.setTitle(deviceName);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.addUpdateListener(animation -> {
            float a = (float) animation.getAnimatedValue();
            mCpbProgress.setProgress(a);
            mTvProgress.setText((int) a + "%");
            if ((int) a == 100) {
                onConnectFail();
            } else {
                showConnectingUi();
            }
        });
        valueAnimator.setDuration(3000);
        valueAnimator.start();

    }

    /**
     * 连接失败的跳转
     */
    private void onConnectFail() {
        Intent intent = new Intent(this, ConnectFailActivity.class);
        intent.putExtra("name", deviceName);
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
            ChangeDeviceNameDialog deviceNameDialog = new ChangeDeviceNameDialog(this);
            deviceNameDialog.setOnSureClick(name -> {

            });
            deviceNameDialog.show(mTvEdit.getText().toString());
        });
    }
}
