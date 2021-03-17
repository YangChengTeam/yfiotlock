package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.ble.FAQInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/6
 */
public class FAQAdapter extends BaseExtendAdapter<FAQInfo> implements LoadMoreModule {

    public FAQAdapter(@Nullable List<FAQInfo> data) {
        super(R.layout.item_faq, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FAQInfo FAQInfo) {
        holder.setText(R.id.tv_name, FAQInfo.getQuestion());
    }
}
