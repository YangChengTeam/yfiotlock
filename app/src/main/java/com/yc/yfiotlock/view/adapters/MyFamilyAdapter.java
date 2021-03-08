package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.FamilyInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyFamilyAdapter extends BaseExtendAdapter<FamilyInfo> {

    public MyFamilyAdapter(@Nullable List data) {
        super(R.layout.item_my_family, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, FamilyInfo FamilyInfo) {
        baseViewHolder.setText(R.id.tv_temp_pwd_name, FamilyInfo.getName());
    }


}
