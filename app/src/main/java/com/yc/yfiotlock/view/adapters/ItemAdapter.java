package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.remote.ItemInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemAdapter extends BaseExtendAdapter<ItemInfo> {

    public ItemAdapter(@Nullable List data) {
        super(R.layout.item_view, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ItemInfo itemInfo) {
        baseViewHolder.setText(R.id.tv_next_name, itemInfo.getName())
                .setText(R.id.tv_next_des, itemInfo.getDes());

        baseViewHolder.setGone(R.id.view_line_next, baseViewHolder.getAdapterPosition() == 0);
    }
}
