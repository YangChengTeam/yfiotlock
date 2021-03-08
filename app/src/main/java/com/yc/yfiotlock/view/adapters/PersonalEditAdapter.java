package com.yc.yfiotlock.view.adapters;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.user.PersonalInfoActivity;
import com.yc.yfiotlock.model.bean.PersonalInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class PersonalEditAdapter extends BaseExtendAdapter<PersonalInfo> {
    public PersonalEditAdapter(@Nullable List<PersonalInfo> data) {
        super(R.layout.item_edit_user_info, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, PersonalInfo personalInfo) {
        holder.setText(R.id.tv_name, personalInfo.getName());
        if (personalInfo.getType() == 0) {
            if (personalInfo.getImg().contains(PersonalInfoActivity.mCropIcon)) {
                Glide.with(getContext())
                        .load(personalInfo.getImg())
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into((ImageView) holder.getView(R.id.iv_pic));
            } else {
                Glide.with(getContext())
                        .load(personalInfo.getImg())
                        .circleCrop()
                        .into((ImageView) holder.getView(R.id.iv_pic));
            }

            holder.setText(R.id.tv_value, "");
        } else {
            Glide.with(getContext()).clear((ImageView) holder.getView(R.id.iv_pic));
            holder.setText(R.id.tv_value, personalInfo.getValue());
        }
        holder.getView(R.id.iv_arrow).setVisibility(personalInfo.isShowArrow() ? View.VISIBLE : View.GONE);
    }
}
