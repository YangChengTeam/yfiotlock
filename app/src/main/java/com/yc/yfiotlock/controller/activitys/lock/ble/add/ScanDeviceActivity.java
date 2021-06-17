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
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.helper.CloudHelper;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.BleUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import butterknife.BindView;

public class ScanDeviceActivity extends BaseAddActivity implements LockBLESender.NotifyCallback {


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

    private BleDevice bleDevice;

    private boolean isFoundOne;
    private boolean isNav2List;
    private boolean isChecking;

    private LockBLESender lockBleSender;
    private ArrayBlockingQueue<BleDevice> bleDevices;
    private CheckThread checkThread;

    private CloudHelper cloudHelper;

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
        cloudHelper = new CloudHelper(this);
        cloudHelper.registerNotify();
        bleDevices = new ArrayBlockingQueue<BleDevice>(1);
        checkThread = new CheckThread();
        checkThread.start();
    }

    @Override
    protected void initViews() {
        super.initViews();
        mInstance = new WeakReference<>(this);
        scan();
    }

    private void scan() {
        LockBLEManager.getInstance().initConfig();
        LockBLEManager.getInstance().scan(this, new LockBLEManager.LockBLEScanCallbck() {
            @Override
            public void onScanStarted() {
                setStartInfo();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (!BleUtil.isFoundDevice(bleDevice.getMac())) {
                    bleDevices.offer(bleDevice);
                }
            }

            @Override
            public void onScanSuccess(List<BleDevice> bleDevices) {
                if (isFoundOne) {
                    setSuccessInfo();
                } else {
                    setFailInfo();
                }
                isFoundOne = false;
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
        checkThread.interrupt();
        cloudHelper.unregisterNotify();
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

    private void setSuccessInfo() {
        mIvScanFlag.clearAnimation();
        mStvRescan.setVisibility(View.VISIBLE);
        mTvScanState.setText("扫描完成");
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


    private class CheckThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted() && !isChecking) {
                try {
                    connect(bleDevices.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void bleCheckLock() {
        String key = CommonUtil.getOriginKey(bleDevice.getMac());
        if (lockBleSender != null) {
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_CHECK_LOCK, LockBLESettingCmd.checkLock(key, key));
        }
    }

    private void connect(BleDevice bleDevice) {
        LockBLEManager.getInstance().connect(bleDevice, new LockBLEManager.LockBLEConnectCallbck() {
            @Override
            public void onConnectStarted() {
            }

            @Override
            public void onDisconnect(BleDevice bleDevice) {

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice) {
                mLoadingDialog.dismiss();
                ScanDeviceActivity.this.bleDevice = bleDevice;
                lockBleSender = new LockBLESender(getContext(), bleDevice, CommonUtil.getOriginKey(bleDevice.getMac()));
                lockBleSender.registerNotify();
                lockBleSender.setNotifyCallback(ScanDeviceActivity.this);
            }

            @Override
            public void onConnectFailed() {
                isChecking = false;
            }
        });
    }

    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHECK_LOCK) {
            isChecking = false;
            lockBleSender.setOpOver(true);
            mLoadingDialog.dismiss();
            bleDevice.setMatch(true);
            LogUtil.msg("key匹配成功");
            if (!isFoundOne) {
                isFoundOne = true;
                nav2List(bleDevice);
            } else {
                EventBus.getDefault().post(bleDevice);
            }
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHECK_LOCK) {
            isChecking = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotify(BleNotifyEvent bleNotifyEvent) {
        if (bleNotifyEvent.getStatus() == BleNotifyEvent.onNotifySuccess) {
            VUiKit.postDelayed(3000, () -> {
                if (CommonUtil.isActivityDestory(getContext())) return;
                if (lockBleSender != null && lockBleSender.isOpOver()) return;
                isChecking = false;
            });
            bleCheckLock();
        }
    }


}
