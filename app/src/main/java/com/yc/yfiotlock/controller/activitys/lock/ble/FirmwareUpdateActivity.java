package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;

import butterknife.BindView;

public class FirmwareUpdateActivity extends BaseBackActivity {

    @BindView(R.id.ll_update)
    View updateView;
    @BindView(R.id.ll_no_update)
    View lastView;

    @BindView(R.id.tv_version)
    TextView versionTv;
    @BindView(R.id.tv_version_fno_update)
    TextView fnuVersionTv;
    @BindView(R.id.tv_new_version)
    TextView newVersionTv;
    @BindView(R.id.tv_desp)
    TextView despTv;

    @BindView(R.id.stv_update)
    View updateBtn;

    private UpdateInfo updateInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_firmware_update;
    }

    @Override
    protected void initVars() {
        super.initVars();
        updateInfo = (UpdateInfo)getIntent().getSerializableExtra("updateInfo");
    }

    @Override
    protected void initViews() {
        super.initViews();
        if(updateInfo != null){
            updateView.setVisibility(View.VISIBLE);
        } else {
            lastView.setVisibility(View.VISIBLE);
        }
    }
}
