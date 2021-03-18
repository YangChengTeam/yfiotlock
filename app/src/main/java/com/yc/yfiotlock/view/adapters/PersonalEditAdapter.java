package com.yc.yfiotlock.view.adapters;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.user.PersonalInfoActivity;
import com.yc.yfiotlock.model.bean.user.PersonalInfo;
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

            RequestBuilder<Drawable> builder = Glide.with(getContext())
                    .load(personalInfo.getImg())
                    .circleCrop()
                    .placeholder(R.mipmap.head_default)
                    .error(R.mipmap.head_default);

            //如果是本地剪切的图片 就需要跳过内存缓存，因为本地图片路径是固定的。如果不跳过缓存，图片就没变化
            if (personalInfo.getImg().contains(PersonalInfoActivity.CROP_ICON_NAME)) {
                builder = builder.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
            }

            builder.into((ImageView) holder.getView(R.id.iv_pic));
            holder.setText(R.id.tv_value, "");
        } else {
            Glide.with(getContext()).clear((ImageView) holder.getView(R.id.iv_pic));
            holder.setText(R.id.tv_value, personalInfo.getValue());
        }
        holder.getView(R.id.iv_arrow).setVisibility(personalInfo.isShowArrow() ? View.VISIBLE : View.GONE);
    }
}
