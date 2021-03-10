package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.user.PersonalInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class PersonalAdapter extends BaseExtendAdapter<PersonalInfo> {

    public PersonalAdapter(@Nullable List<PersonalInfo> data) {
        super(R.layout.item_my, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, PersonalInfo personalInfo) {
        holder.setImageResource(R.id.iv_pic, personalInfo.getResId());
        holder.setText(R.id.tv_name, personalInfo.getName());
    }
}
