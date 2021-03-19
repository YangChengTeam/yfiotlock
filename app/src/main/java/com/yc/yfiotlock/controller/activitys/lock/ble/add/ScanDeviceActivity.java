package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.utils.AnimatinUtil;

import butterknife.BindView;

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
        setClick(mStvRescan, this::setScanStartUi);
        setClick(mTvScanQa, () -> startActivity(new Intent(this, QaActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScanStartUi();
    }

    private void setScanStartUi() {
        AnimatinUtil.rotate(mIvScanFlag);
        mStvRescan.setVisibility(View.GONE);
        mTvScanState.setText("开始扫描");
        mTvScanQa.setText("扫描不到怎么办？");
        if (success) {
            VUiKit.postDelayed(2000, this::setScanStopUi);
            success = false;
        } else {
            VUiKit.postDelayed(2000, () -> {
                startActivity(new Intent(this, DeviceListActivity.class));
                setScanStopUi();
            });
            success = true;
        }
    }

    private boolean success = true;

    private void setScanStopUi() {
        mIvScanFlag.clearAnimation();
        mStvRescan.setVisibility(View.VISIBLE);
        mTvScanState.setText("未发现附近有可添加设备");
        mTvScanQa.setText("无法扫到设备怎么办？");
    }
}
