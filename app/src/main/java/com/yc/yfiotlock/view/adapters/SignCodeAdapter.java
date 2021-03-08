package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class SignCodeAdapter extends BaseExtendAdapter<String> {
    public SignCodeAdapter(@Nullable List<String> data) {
        super(R.layout.item_code, data);
    }

    @Override
    public void onBindViewHolder(@NotNull BaseViewHolder holder, int position, @NotNull List<Object> payloads) {

        if (payloads.size() > 0) {
            holder.setText(R.id.tv_number, getData().get(position));
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_number, s + "");
    }
}
