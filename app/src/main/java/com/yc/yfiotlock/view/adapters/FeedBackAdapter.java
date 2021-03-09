package com.yc.yfiotlock.view.adapters;

import android.net.Uri;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public class FeedBackAdapter extends BaseExtendAdapter<Uri> {
    public FeedBackAdapter(@Nullable List<Uri> data) {
        super(R.layout.item_feed_back, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Uri s) {
        CardView cardView = holder.getView(R.id.cv_pic);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) cardView.getLayoutParams();
        layoutParams.width = (ScreenUtil.getWidth(getContext()) - ScreenUtil.dip2px(getContext(), 40)) / 3;
        layoutParams.height = (ScreenUtil.getWidth(getContext()) - ScreenUtil.dip2px(getContext(), 40)) / 3;
        cardView.setLayoutParams(layoutParams);
        ImageView imageView = holder.getView(R.id.iv_pic);
        imageView.setTag(s);
        Glide.with(getContext())
                .load(s.equals(Uri.parse("default")) ? R.mipmap.feedback_add_pic : s)
                .centerCrop()
                .into(imageView);

        RxView.clicks(imageView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(unit -> {
            if (getOnItemChildClickListener() != null) {
                getOnItemChildClickListener().onItemChildClick(this, imageView, holder.getLayoutPosition());
            }
        });

        holder.setVisible(R.id.iv_cancel, !s.equals(Uri.parse("default")));
        RxView.clicks(holder.getView(R.id.iv_cancel)).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(unit -> {
            if (getOnItemChildClickListener() != null) {
                getOnItemChildClickListener().onItemChildClick(this, holder.getView(R.id.iv_cancel), holder.getLayoutPosition());
            }
        });
    }

    public int getDefaultCount() {
        int count = 0;
        for (Uri s : getData()) {
            if (s.equals(Uri.parse("default"))) {
                count++;
            }
        }
        return count;
    }
}
