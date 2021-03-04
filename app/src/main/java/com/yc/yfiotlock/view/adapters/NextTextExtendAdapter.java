package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.NextTextInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NextTextExtendAdapter extends BaseExtendAdapter<NextTextInfo> {

    public NextTextExtendAdapter(@Nullable List data) {
        super(R.layout.item_next_text, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, NextTextInfo nextTextInfo) {
        baseViewHolder.setText(R.id.tv_next_name, nextTextInfo.getName())
                .setText(R.id.tv_next_des, nextTextInfo.getDes());
    }


}
