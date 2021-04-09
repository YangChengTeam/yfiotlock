package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 扫描结果页
 *
 * @author Dullyoung
 */
public class DeviceListActivity extends BaseAddActivity {
    @BindView(R.id.tv_scan_title)
    TextView mTvScanTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;
    @BindView(R.id.stv_scan)
    SuperTextView mStvScan;

    private DeviceAdapter mDeviceAdapter;

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
            LockInfo tlockInfo = (LockInfo) adapter.getData().get(position);
            connect(tlockInfo.getBleDevice());
        });
    }


    private void connect(BleDevice bleDevice) {
        LockBLEManager.connect(bleDevice, new LockBLEManager.LockBLEConnectCallbck() {
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
                nav2Connect(bleDevice);
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

    @Override
    protected void bindClick() {
        setClick(mStvScan, () -> {
            finish();
        });
    }

    private class DeviceAdapter extends BaseExtendAdapter<LockInfo> {
        public DeviceAdapter(@Nullable List<LockInfo> data) {
            super(R.layout.item_scan_device, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, LockInfo lockInfo) {
            holder.setText(R.id.tv_name, lockInfo.getName());
        }
    }


}
