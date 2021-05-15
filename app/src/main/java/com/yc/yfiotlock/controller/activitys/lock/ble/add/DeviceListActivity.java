package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 扫描结果页
 *
 * @author Dullyoung
 */
public class DeviceListActivity extends BaseAddActivity implements LockBLESender.NotifyCallback {
    @BindView(R.id.tv_scan_title)
    TextView mTvScanTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;
    @BindView(R.id.stv_scan)
    SuperTextView mStvScan;

    protected LockBLESender lockBleSender;
    private BleDevice bleDevice;
    private DeviceAdapter mDeviceAdapter;
    private LockInfo lockInfo;


    private static WeakReference<DeviceListActivity> mInstance;

    public static void safeFinish() {
        if (mInstance != null && mInstance.get() != null) {
            mInstance.get().finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_add_activity_device_list;
    }

    @Override
    protected void initViews() {
        mInstance = new WeakReference<>(this);
        super.initViews();
        setRvDevices();
    }

    private void setRvDevices() {
        List<LockInfo> lockInfos = new ArrayList<>();
        BleDevice bleDevice = getIntent().getParcelableExtra("bleDevice");
        LockInfo lockInfo = new LockInfo(bleDevice.getName());
        lockInfo.setBleDevice(bleDevice);
        lockInfo.setMacAddress(bleDevice.getMac());
        lockInfos.add(lockInfo);
        mDeviceAdapter = new DeviceAdapter(lockInfos);
        mRvDevices.setAdapter(mDeviceAdapter);
        mRvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvDevices);
        mDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceListActivity.this.lockInfo = (LockInfo) adapter.getData().get(position);
            connect(DeviceListActivity.this.lockInfo.getBleDevice(), DeviceListActivity.this.lockInfo.getKey());
        });
    }

    private void connect(BleDevice bleDevice, String key) {
        LockBLEManager.getInstance().connect(bleDevice, new LockBLEManager.LockBLEConnectCallbck() {
            @Override
            public void onConnectStarted() {
                mLoadingDialog.show("正在连接");
            }

            @Override
            public void onDisconnect(BleDevice bleDevice) {

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice) {
                mLoadingDialog.dismiss();
                DeviceListActivity.this.bleDevice = bleDevice;
                lockBleSender = new LockBLESender(getContext(), bleDevice, key);
                lockBleSender.registerNotify();
                lockBleSender.setNotifyCallback(DeviceListActivity.this);
            }

            @Override
            public void onConnectFailed() {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), "连接失败");
            }
        });
    }

    private void nav2Connect(BleDevice bleDevice) {
        Intent intent = new Intent(this, ConnectActivity.class);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("family", familyInfo);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(BleDevice bleDevice) {
        LockInfo lockInfo = new LockInfo(bleDevice.getName());
        lockInfo.setBleDevice(bleDevice);
        lockInfo.setMacAddress(bleDevice.getMac());
        mDeviceAdapter.addData(lockInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotify(BleNotifyEvent bleNotifyEvent) {
        if (bleNotifyEvent.getStatus() == BleNotifyEvent.onNotifySuccess) {
            mLoadingDialog.show("检测中...");
            bleCheckLock();
        }
    }


    @Override
    protected void bindClick() {
        setClick(mStvScan, () -> {
            finish();
            VUiKit.postDelayed(300, () -> {
                EventBus.getDefault().post(new ReScanEvent());
            });
        });
    }


    private static class DeviceAdapter extends BaseExtendAdapter<LockInfo> {
        public DeviceAdapter(@Nullable List<LockInfo> data) {
            super(R.layout.item_scan_device, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, LockInfo lockInfo) {
            holder.setText(R.id.tv_name, lockInfo.getName());
        }
    }

    private void bleCheckLock() {
        if (lockBleSender != null) {
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_CHECK_LOCK, LockBLESettingCmd.checkLock(lockInfo.getOrigenKey(), lockInfo.getKey()));
        }
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHECK_LOCK) {
            mLoadingDialog.dismiss();
            LogUtil.msg("key匹配成功");
            nav2Connect(bleDevice);
        }
    }

    private int retryCount = 3;
    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHECK_LOCK) {
            mLoadingDialog.dismiss();
            LogUtil.msg("key匹配失败");
            if (retryCount-- > 0) {
                bleCheckLock();
            } else {
                retryCount = 3;
                ToastCompat.show(this, "设备已被添加");
            }
        }
    }

}
