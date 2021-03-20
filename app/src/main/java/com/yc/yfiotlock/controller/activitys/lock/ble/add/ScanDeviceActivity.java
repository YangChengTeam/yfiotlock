package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.utils.AnimatinUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ScanDeviceActivity extends BaseAddActivity {


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
        scan();
    }

    private void scan() {
        LockBLEManager.initConfig();
        LockBLEManager.scan(this, new LockBLEManager.LockBLEScanCallbck() {
            @Override
            public void onScanStarted() {
                setStartInfo();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }

            @Override
            public void onScanSuccess(List<BleDevice> bleDevices) {
                finish();
                nav2List((ArrayList<BleDevice>) bleDevices);
            }

            @Override
            public void onScanFailed() {
                setFailInfo();
            }
        });
    }

    @Override
    protected void bindClick() {
        setClick(mStvRescan, () -> {
            setStartInfo();
            VUiKit.postDelayed(6000, this::setFailInfo);
        });
        setClick(mTvScanQa, () -> startActivity(new Intent(this, QaActivity.class)));
    }

    private void setStartInfo() {
        AnimatinUtil.rotate(mIvScanFlag);
        mStvRescan.setVisibility(View.GONE);
        mTvScanState.setText("正在扫描...");
        mTvScanQa.setText("扫描不到怎么办？");
    }

    private void setFailInfo() {
        mIvScanFlag.clearAnimation();
        mStvRescan.setVisibility(View.VISIBLE);
        mTvScanState.setText("未发现附近有可添加设备");
        mTvScanQa.setText("无法扫到设备怎么办？");
    }

    private void nav2List(ArrayList<BleDevice> bleDevices) {
        Intent intent = new Intent(this, DeviceListActivity.class);
        intent.putParcelableArrayListExtra("bleDevices",  bleDevices);
        intent.putExtra("family", familyInfo);
        startActivity(intent);
    }
}
