package com.yc.yfiotlock.controller.activitys.lock.ble;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.utils.CacheUtils;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DeviceInfoActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_device_info)
    RecyclerView mRvDeviceInfo;

    private DeviceInfo deviceInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_device_info;
    }

    @Override
    protected void initVars() {
        super.initVars();
        deviceInfo = CacheUtils.getCache("deviceInfo", DeviceInfo.class);
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        setRvDeviceInfo();
    }

    private ItemInfoAdapter mAdapter;

    private void setRvDeviceInfo() {
        mAdapter = new ItemInfoAdapter(null);
        mRvDeviceInfo.setAdapter(mAdapter);
        mRvDeviceInfo.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        CommonUtils.setItemDivider(getContext(), mRvDeviceInfo);
        List<ItemInfo> itemInfos = new ArrayList<>();
        itemInfos.add(new ItemInfo("固件版本", "V20:R6.00.9.12"));
        itemInfos.add(new ItemInfo("协议版本", "2.7"));
        itemInfos.add(new ItemInfo("注册时间", "2020-10-28 16:04:02"));
        itemInfos.add(new ItemInfo("剩余电量", "高"));
        itemInfos.add(new ItemInfo("设备id", "BOF8937BAC56"));
        mAdapter.setNewInstance(itemInfos);
    }

    public class ItemInfo {
        private String name;
        private String value;

        public ItemInfo(String name, String value){
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