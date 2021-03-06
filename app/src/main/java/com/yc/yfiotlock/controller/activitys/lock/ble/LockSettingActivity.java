package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.SettingInfo;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.view.adapters.SettingAdapter;
import com.yc.yfiotlock.view.widgets.SettingSoundView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LockSettingActivity extends BaseBackActivity {
    @BindView(R.id.rv_setting)
    RecyclerView mRvSetting;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_setting;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRvSetting();
    }

    private SettingAdapter mSettingAdapter;

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
            switch (position) {
                case 1:
                    startActivity(new Intent(this, DeviceInfoActivity.class));
                    break;
                case 2:
                    Intent intent = new Intent(this, EditDeviceNameActivity.class);
                    intent.putExtra("name", settingInfo.getValue());
                    startActivity(intent);
                    break;
            }
        });
        CommonUtils.setItemDivider(getContext(), mRvSetting);
        List<SettingInfo> settingInfos = new ArrayList<>();
        settingInfos.add(new SettingInfo("报警管理", ""));
        settingInfos.add(new SettingInfo("设备信息", ""));
        settingInfos.add(new SettingInfo("设备名称", "智能门锁09"));
        settingInfos.add(new SettingInfo("安全设置", ""));
        settingInfos.add(new SettingInfo("固件升级", ""));
        settingInfos.add(new SettingInfo("帮助与反馈", ""));
        mSettingAdapter.setNewInstance(settingInfos);
        mSettingAdapter.setHeaderView(new SettingSoundView(this));
    }

}
