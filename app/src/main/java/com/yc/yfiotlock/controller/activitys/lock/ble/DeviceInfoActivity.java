package com.yc.yfiotlock.controller.activitys.lock.ble;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DeviceInfoActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {


    @BindView(R.id.rv_device_info)
    RecyclerView mRvDeviceInfo;

    private DeviceInfo deviceInfo;
    private ItemInfoAdapter mAdapter;
    private LockBLESend lockBleSend;

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_device_info;
    }

    @Override
    protected void initVars() {
        super.initVars();
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        lockBleSend = new LockBLESend(this, LockIndexActivity.getInstance().getBleDevice());
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRv();

        if (lockBleSend.isConnected()) {
            bleGetBattery();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(this);
            lockBleSend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
        }
    }

    private void setRv() {
        mAdapter = new ItemInfoAdapter(null);
        mRvDeviceInfo.setAdapter(mAdapter);
        mRvDeviceInfo.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        CommonUtil.setItemDivider(getContext(), mRvDeviceInfo);
        List<ItemInfo> itemInfos = new ArrayList<>();
        itemInfos.add(new ItemInfo("固件版本", deviceInfo.getFirmwareVersion()));
        itemInfos.add(new ItemInfo("协议版本", deviceInfo.getProtocolVersion()));
        itemInfos.add(new ItemInfo("注册时间", CommonUtil.formatTime(deviceInfo.getRegtime())));
        itemInfos.add(new ItemInfo("剩余电量", deviceInfo.getBattery() + "%"));
        itemInfos.add(new ItemInfo("设备id", deviceInfo.getMacAddress().replaceAll(":", "")));
        mAdapter.setNewInstance(itemInfos);
    }

    private void bleGetBattery() {
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.getBattery(this);
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_GET_BATTERY, bytes, true);
        }
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_BATTERY) {
            int battery = lockBLEData.getExtra()[0];
            MMKV.defaultMMKV().putInt("battery", battery);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {

    }

    public class ItemInfo {
        private String name;
        private String value;

        public ItemInfo(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private class ItemInfoAdapter extends BaseExtendAdapter<ItemInfo> {
        public ItemInfoAdapter(@Nullable List<ItemInfo> data) {
            super(R.layout.item_device_info, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ItemInfo deviceInfo) {
            holder.setText(R.id.tv_name, deviceInfo.getName());
            holder.setText(R.id.tv_value, deviceInfo.getValue());
        }
    }

}