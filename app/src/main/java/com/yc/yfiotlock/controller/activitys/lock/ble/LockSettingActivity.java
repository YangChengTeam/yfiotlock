package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.SettingSoundView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LockSettingActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {
    @BindView(R.id.rv_setting)
    RecyclerView mRvSetting;

    private DeviceInfo lockInfo;
    private SettingAdapter mSettingAdapter;
    private LockBLESend lockBleSend;
    private SettingSoundView headView;
    private int volume;
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_setting;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        BleDevice bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBleSend = new LockBLESend(this, bleDevice);
        lockBleSend.setNotifyCallback(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRvSetting();
    }

    private void bleSetVolume(int volume) {
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.changeVolume(this, volume);
            lockBleSend.send((byte) 0x01, (byte) 0x08, bytes);
        }
    }

    @Override
    protected void bindClick() {
        setClick(R.id.stv_del, () -> {
            //todo delete device
        });
    }


    private void setRvSetting() {
        mSettingAdapter = new SettingAdapter(null);
        mRvSetting.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mRvSetting.setAdapter(mSettingAdapter);
        mSettingAdapter.setOnItemClickListener((adapter, view, position) -> {
            SettingInfo settingInfo = mSettingAdapter.getData().get(position);
            switch (settingInfo.getName()) {
                case "报警管理":
                    startActivity(new Intent(this, AlarmOpenLockManagerActivity.class));
                    break;
                case "设备信息":
                    Intent deviceInfoIntent = new Intent(this, DeviceInfoActivity.class);
                    deviceInfoIntent.putExtra("device", lockInfo);
                    startActivity(deviceInfoIntent);
                    break;
                case "设备名称":
                    Intent intent = new Intent(this, DeviceNameEditActivity.class);
                    intent.putExtra("name", lockInfo.getName());
                    startActivity(intent);
                    break;
                case "安全设置":
                    Intent safeIntent = new Intent(this, SafePwdSettingActivity.class);
                    safeIntent.putExtra("device", lockInfo);
                    startActivity(safeIntent);
                    break;
                case "帮助与反馈":
                    startActivity(new Intent(this, FAQActivity.class));
                    break;
                default:
                    break;
            }
        });
        CommonUtil.setItemDivider(getContext(), mRvSetting);
        List<SettingInfo> settingInfos = new ArrayList<>();
        settingInfos.add(new SettingInfo("报警管理", ""));
        settingInfos.add(new SettingInfo("设备信息", ""));
        settingInfos.add(new SettingInfo("设备名称", lockInfo.getName()));
        settingInfos.add(new SettingInfo("安全设置", ""));
        settingInfos.add(new SettingInfo("帮助与反馈", ""));
        mSettingAdapter.setNewInstance(settingInfos);
        headView = new SettingSoundView(this);
        headView.setOnSelectChangeListener(new SettingSoundView.OnSelectChangeListener() {
            @Override
            public void onChange(int index) {
                volume = index;
                bleSetVolume(volume);
            }
        });
        mSettingAdapter.setHeaderView(headView);
    }

    // 

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

    @Override
    public void onNotifyReady() {

    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x08) {
            headView.setVolume(volume);
            ToastCompat.show(getContext(), "设置成功");
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x01 && lockBLEData.getScmd() == (byte) 0x08) {
            ToastCompat.show(getContext(), "设置失败");
            headView.resetVolume();
        }
    }

    private class SettingAdapter extends BaseExtendAdapter<SettingInfo> {
        public SettingAdapter(@Nullable List<SettingInfo> data) {
            super(R.layout.item_setting, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, SettingInfo settingInfo) {
            holder.setText(R.id.tv_name, settingInfo.getName());
            holder.setText(R.id.tv_value, settingInfo.getValue());
        }
    }

    private class SettingInfo {
        private String name;
        private String value;

        public SettingInfo(String name, String value) {
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

}
