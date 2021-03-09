package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.user.AboutInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class AboutAdapter extends BaseExtendAdapter<AboutInfo> {
    public AboutAdapter(@Nullable List<AboutInfo> data) {
        super(R.layout.item_about_us, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, AboutInfo aboutInfo) {
        holder.setText(R.id.tv_name, aboutInfo.getName());
        holder.setText(R.id.tv_value, aboutInfo.getValue());
    }
}
