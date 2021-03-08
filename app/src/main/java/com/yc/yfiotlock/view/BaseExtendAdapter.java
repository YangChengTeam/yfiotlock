package com.yc.yfiotlock.view;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.constant.Config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public abstract class BaseExtendAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public BaseExtendAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    @Override
    protected void bindViewClickListener(@NotNull BaseViewHolder viewHolder, int viewType) {
        RxView.clicks(viewHolder.itemView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            int position = viewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            position -= getHeaderLayoutCount();
            setOnItemClick(viewHolder.itemView, position);
        });

        RxView.longClicks(viewHolder.itemView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            int position = viewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            position -= getHeaderLayoutCount();
            setOnItemLongClick(viewHolder.itemView, position);
        });

        if (getOnItemChildClickListener() != null) {
            for (int id : getChildClickViewIds()
            ) {
                RxView.clicks(viewHolder.getView(id)).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                    int position = viewHolder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }
                    position -= getHeaderLayoutCount();
                    setOnItemChildClick(viewHolder.getView(id), position);
                });
            }
        }
    }
}
