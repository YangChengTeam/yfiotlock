package com.yc.yfiotlock.view.adapters;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IndexDeviceAdapter extends BaseExtendAdapter<DeviceInfo> {

    public IndexDeviceAdapter(@Nullable List<DeviceInfo> data) {
        super(R.layout.lock_ble_item_index_device, data);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, DeviceInfo deviceInfo) {
        holder.setText(R.id.tv_name, deviceInfo.getName());
        View card = holder.getView(R.id.card);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) card.getLayoutParams();
        if (holder.getAdapterPosition() % 2 == 0) {
            layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.rightMargin = ScreenUtil.dip2px(getContext(), 5);
            layoutParams.leftToLeft =  -1;
        } else {
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.leftMargin = ScreenUtil.dip2px(getContext(), 5);
            layoutParams.rightToRight = -1;
        }

        if (holder.getAdapterPosition() == getData().size() - 1) {
            holder.setVisible(R.id.ll_default_add, true);
            holder.setVisible(R.id.ll_device_info, false);
        } else {
            holder.setVisible(R.id.ll_device_info, true);
            holder.setVisible(R.id.ll_default_add, false);
        }
    }
}
