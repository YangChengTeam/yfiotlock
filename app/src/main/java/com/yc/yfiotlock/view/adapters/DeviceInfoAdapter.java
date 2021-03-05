package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class DeviceInfoAdapter extends BaseExtendAdapter<DeviceInfo> {
    public DeviceInfoAdapter(@Nullable List<DeviceInfo> data) {
        super(R.layout.item_device_info, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DeviceInfo deviceInfo) {
        holder.setText(R.id.tv_name, deviceInfo.getItemName());
        holder.setText(R.id.tv_value, deviceInfo.getValue());
    }
}
