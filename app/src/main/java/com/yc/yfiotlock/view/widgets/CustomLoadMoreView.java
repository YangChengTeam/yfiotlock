package com.yc.yfiotlock.view.widgets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;

import org.jetbrains.annotations.NotNull;

public final class CustomLoadMoreView extends BaseLoadMoreView {

    @NotNull
    @Override
    public View getRootView(@NotNull ViewGroup parent) {
        // 整个 LoadMore 布局
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_custom_load_more, parent, false);
    }

    @NotNull
    @Override
    public View getLoadingView(@NotNull BaseViewHolder holder) {
        // 布局中 “加载中”的View
        return holder.findView(R.id.load_custom_more_loading_view);
    }

    @NotNull
    @Override
    public View getLoadComplete(@NotNull BaseViewHolder holder) {
        // 布局中 “当前一页加载完成”的View
        return holder.findView(R.id.load_custom_more_load_complete_view);
    }

    @NotNull
    @Override
    public View getLoadEndView(@NotNull BaseViewHolder holder) {
        // 布局中 “全部加载结束，没有数据”的View
        return holder.findView(R.id.load_custom_more_load_end_view);
    }

    @NotNull
    @Override
    public View getLoadFailView(@NotNull BaseViewHolder holder) {
        // 布局中 “加载失败”的View
        return holder.findView(R.id.load_custom_more_load_fail_view);
    }
}
