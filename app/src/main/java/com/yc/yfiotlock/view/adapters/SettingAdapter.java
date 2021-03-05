package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.SettingInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class SettingAdapter extends BaseExtendAdapter<SettingInfo> {
    public SettingAdapter(@Nullable List<SettingInfo> data) {
        super(R.layout.item_setting, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SettingInfo settingInfo) {
        holder.setText(R.id.tv_name, settingInfo.getName());
        holder.setText(R.id.tv_value, settingInfo.getValue());
    }
}
