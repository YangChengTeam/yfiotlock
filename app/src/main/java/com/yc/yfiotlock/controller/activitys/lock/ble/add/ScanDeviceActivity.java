package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    private boolean isFoundOne;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_scan_device;
    }

    @Override
    protected void initVars() {
        super.initVars();
    }

    @Override
    protected void initViews() {
        super.initViews();
        scan();
    }

    private HashMap<String, BleDevice> deviceHashMap = new LinkedHashMap<>();

    private void scan() {
        LockBLEManager.initConfig();
        LockBLEManager.scan(this, new LockBLEManager.LockBLEScanCallbck() {
            @Override
            public void onScanStarted() {
                setStartInfo();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (deviceHashMap.get(bleDevice.getMac()) != null || !LockBLEManager.DEVICE_NAME.equals(bleDevice.getName() + "")) {
                    return;
                }
                if (!LockBLEManager.isFoundDevice(bleDevice.getMac())) {
                    if (!isFoundOne) {
                        isFoundOne = true;
                        deviceHashMap.put(bleDevice.getMac(), bleDevice);
                        nav2List(bleDevice);
                    } else {
                        deviceHashMap.put(bleDevice.getMac(), bleDevice);
                        EventBus.getDefault().post(bleDevice);
                    }
                }
            }

            @Override
            public void onScanSuccess(List<BleDevice> bleDevices) {
                if (isFoundOne) {
                    finish();
                    deviceHashMap.clear();
                    deviceHashMap = null;
                    isFoundOne = false;
                } else {
                    setFailInfo();
                }
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

    private void nav2List(BleDevice bleDevice) {
        Intent intent = new Intent(this, DeviceListActivity.class);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("family", familyInfo);
        startActivity(intent);
    }
}
