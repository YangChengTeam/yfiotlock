package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import butterknife.BindView;

public class FirmwareUpdateNextActivity extends BaseBackActivity {

    @BindView(R.id.ll_update_success)
    View updateSuccessView;
    @BindView(R.id.fl_install)
    View installView;
    @BindView(R.id.cpb_progress)
    CircularProgressBar circularProgressBar;

    @BindView(R.id.tv_version)
    TextView versionTv;
    @BindView(R.id.tv_version_update)
    TextView updateVersionTv;
    @BindView(R.id.tv_new_version)
    TextView newVersionTv;
    @BindView(R.id.tv_desp)
    TextView despTv;

    LockBLESend lockBLESend;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_firmware_update_next;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockBLESend = new LockBLESend(this,  LockIndexActivity.getInstance().getBleDevice());
    }
}
