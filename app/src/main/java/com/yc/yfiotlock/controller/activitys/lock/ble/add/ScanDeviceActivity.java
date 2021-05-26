package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.BleUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
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
    private boolean isNav2List;

    private static WeakReference<ScanDeviceActivity> mInstance;

    public static void safeFinish() {
        if (mInstance != null && mInstance.get() != null) {
            mInstance.get().finish();
        }
    }

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
        mInstance = new WeakReference<>(this);
        scan();
    }

    private HashMap<String, BleDevice> deviceHashMap = new HashMap<>();

    private void scan() {
        if (deviceHashMap == null) {
            deviceHashMap = new HashMap<>();
        }
        LockBLEManager.getInstance().initConfig();
        LockBLEManager.getInstance().scan(this, new LockBLEManager.LockBLEScanCallbck() {
            @Override
            public void onScanStarted() {
                setStartInfo();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (!BleUtil.isFoundDevice(bleDevice.getMac())) {
                    if (!isFoundOne) {
                        isFoundOne = true;
                        deviceHashMap.put(bleDevice.getMac(), bleDevice);
                        nav2List(bleDevice);
                    } else {
                        deviceHashMap.put(bleDevice.getMac(), bleDevice);
                        VUiKit.postDelayed(1000, () -> {
                            EventBus.getDefault().post(bleDevice);
                        });
                    }
                } else {
                    LogUtil.msg("已经添加的锁不进入列表:" + bleDevice.getKey());
                }
            }

            @Override
            public void onScanSuccess(List<BleDevice> bleDevices) {
                if (isFoundOne) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReScan(ReScanEvent object) {
        isFoundOne = false;
        LockBLEManager.getInstance().stopScan();
        scan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LockBLEManager.getInstance().stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNav2List) {
            setFailInfo();
            isNav2List = false;
        }
    }

    @Override
    protected void bindClick() {
        setClick(mStvRescan, () -> {
            LockBLEManager.getInstance().stopScan();
            isFoundOne = false;
            setStartInfo();
            scan();
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
        if (isNav2List) return;
        isNav2List = true;
        Intent intent = new Intent(this, DeviceListActivity.class);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("family", familyInfo);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // GPS授权回调
        if (requestCode == LockBLEManager.REQUEST_GPS) {
            if (BleUtil.checkGPSIsOpen(this)) {
                scan();
            }
        }
        // 处理授权回调
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }
}
