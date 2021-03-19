package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 扫描结果页
 *
 * @author Dullyoung
 */
public class DeviceListActivity extends BaseBackActivity {
    @BindView(R.id.tv_scan_title)
    TextView mTvScanTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;
    @BindView(R.id.stv_scan)
    SuperTextView mStvScan;


    private DeviceAdapter mDeviceAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_add_activity_device_list;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRvDevices();
    }

    private void setRvDevices() {
        List<LockInfo> lockInfos = new ArrayList<>();
        List<BleDevice> bleDevices = getIntent().getParcelableArrayListExtra("bleDevices");
        for (BleDevice bleDevice : bleDevices) {
            LockInfo lockInfo = new LockInfo(bleDevice.getName());
            lockInfo.setBleDevice(bleDevice);
            lockInfo.setMacAddress(bleDevice.getMac());
            lockInfos.add(lockInfo);
        }
        mDeviceAdapter = new DeviceAdapter(lockInfos);
        mRvDevices.setAdapter(mDeviceAdapter);
        mRvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvDevices);
        mDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {
            LockInfo lockInfo = (LockInfo) adapter.getData().get(position);
            connect(lockInfo.getBleDevice());
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
                LockBLEManager.setMtu(bleDevice);
            }

            @Override
            public void onConnectFailed() {
                mLoadingDialog.dismiss();
                ToastCompat.show(DeviceListActivity.this, "连接失败");
            }
        });
    }

    private void nav2Connect(BleDevice bleDevice) {
        Intent intent = new Intent(this, ConnectActivity.class);
        intent.putExtra("bleDevice", bleDevice);
        startActivity(intent);
    }

    private void nav2Scan() {
        Intent intent = new Intent(this, ScanDeviceActivity.class);
        startActivity(intent);
    }

    @Override
    protected void bindClick() {
        setClick(mStvScan, () -> {
            nav2Scan();
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
