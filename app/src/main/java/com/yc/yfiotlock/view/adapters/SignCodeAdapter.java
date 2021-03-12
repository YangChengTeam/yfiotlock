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

    public SignCodeAdapter(int layout, @Nullable List<String> data) {
        super(layout, data);
    }

    @Override
    public void onBindViewHolder(@NotNull BaseViewHolder holder, int position, @NotNull List<Object> payloads) {

        if (payloads.size() > 0) {
            setNumberText(holder, getData().get(position));
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    private boolean showText = true;

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    private void setNumberText(@NotNull BaseViewHolder holder, String s) {
        if (showText) {
            holder.setText(R.id.tv_number, s + "");
        } else {
            holder.setVisible(R.id.tv_number, !"".equals(s));
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, String s) {
        setNumberText(holder, s);
    }
}
