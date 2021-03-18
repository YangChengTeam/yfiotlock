package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanDeviceActivity extends BaseBackActivity {


    @BindView(R.id.iv_scan_bg)
    ImageView mIvScanBg;
    @BindView(R.id.iv_scan_flag)
    ImageView mIvScanFlag;
    @BindView(R.id.tv_scan_state)
    TextView mTvScanState;
    @BindView(R.id.tv_scan_qa)
    TextView mTvScanQa;
    @BindView(R.id.stv_rescan)
    SuperTextView mStvRescan;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_scan_device;
    }

    @Override
    protected void initViews() {
        super.initViews();

    }

    @Override
    protected void bindClick() {
        setClick(mStvRescan, () -> {
            setScanStartUi("开始扫描", "扫描不到怎么办？");
            VUiKit.postDelayed(6000, () -> {
                setScanStopUi("未发现附近有可添加设备", "无法扫到设备怎么办？");
            });
        });
        setClick(mTvScanQa, () -> startActivity(new Intent(this, QaActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScanStartUi("开始扫描", "扫描不到怎么办？");
        VUiKit.postDelayed(6000, () -> {
            setScanStopUi("未发现附近有可添加设备", "无法扫到设备怎么办？");
        });
    }

    private void setScanStartUi(String title, String qa) {
        AnimatinUtil.rotate(mIvScanFlag);
        mStvRescan.setVisibility(View.GONE);
        mTvScanState.setText(title);
        mTvScanQa.setText(qa);
    }

    private void setScanStopUi(String title, String qa) {
        mIvScanFlag.clearAnimation();
        mStvRescan.setVisibility(View.VISIBLE);
        mTvScanState.setText(title);
        mTvScanQa.setText(qa);
    }
}
