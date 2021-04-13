package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;

import java.util.concurrent.locks.Lock;

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

    private UpdateInfo updateInfo;
    private DeviceInfo deviceInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_firmware_update;
    }

    @Override
    protected void initVars() {
        super.initVars();
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        updateInfo = (UpdateInfo) getIntent().getSerializableExtra("updateInfo");
    }

    @Override
    protected void initViews() {
        super.initViews();

        setInfo();
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(R.id.stv_update, this::nav2next);
    }

    private void nav2next(){
        Intent updateIntent = new Intent(this, FirmwareUpdateNextActivity.class);
        updateIntent.putExtra("updateInfo", updateInfo);
        startActivity(updateIntent);
    }

    private void setInfo() {
        if (updateInfo != null) {
            updateView.setVisibility(View.VISIBLE);
            despTv.setText(updateInfo.getDesc());
            newVersionTv.setText(Html.fromHtml("升级" + "(<font color='#999999'>" + updateInfo.getVersion() + "</font>)"));
            versionTv.setText("当前版本"+deviceInfo.getFirmwareVersion());
        } else {
            lastView.setVisibility(View.VISIBLE);
            fnuVersionTv.setText(deviceInfo.getFirmwareVersion());
        }
    }
}
