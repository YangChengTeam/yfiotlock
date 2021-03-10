package com.yc.yfiotlock.controller.activitys.lock.ble;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.view.adapters.DeviceInfoAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DeviceInfoActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_device_info)
    RecyclerView mRvDeviceInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_device_info;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        setRvDeviceInfo();
    }

    private DeviceInfoAdapter mAdapter;

    private void setRvDeviceInfo() {
        mAdapter = new DeviceInfoAdapter(null);
        mRvDeviceInfo.setAdapter(mAdapter);
        mRvDeviceInfo.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        CommonUtils.setItemDivider(getContext(), mRvDeviceInfo);
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        deviceInfos.add(new DeviceInfo("固件版本", "V20:R6.00.9.12"));
        deviceInfos.add(new DeviceInfo("协议版本", "2.7"));
        deviceInfos.add(new DeviceInfo("注册时间", "2020-10-28 16:04:02"));
        deviceInfos.add(new DeviceInfo("剩余电量", "高"));
        deviceInfos.add(new DeviceInfo("设备id", "BOF8937BAC56"));
        mAdapter.setNewInstance(deviceInfos);
    }

}