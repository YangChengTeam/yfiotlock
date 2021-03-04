package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.view.BaseAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IndexDeviceAdapter<DeviceInfo> extends BaseAdapter<DeviceInfo, BaseViewHolder> {

    public IndexDeviceAdapter(@Nullable List<DeviceInfo> data) {
        super(R.layout.lock_ble_item_index_device, data);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, DeviceInfo deviceInfo) {
        if (holder.getAdapterPosition() == 0) {
            holder.setVisible(R.id.ll_default_add, true);
            holder.setVisible(R.id.ll_device_info, false);
        } else {
            holder.setVisible(R.id.ll_device_info, true);
            holder.setVisible(R.id.ll_default_add, false);
        }
    }
}
