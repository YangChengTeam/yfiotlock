package com.yc.yfiotlock.view.adapters;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyFamilyAdapter extends BaseExtendAdapter<FamilyInfo> {

    public MyFamilyAdapter(@Nullable List data) {
        super(R.layout.item_my_family, data);

        addChildClickViewIds(R.id.iv_family_number_default, R.id.tv_family_number_default, R.id.tv_family_delete);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, FamilyInfo familyInfo) {
        baseViewHolder.setText(R.id.tv_my_family_name, familyInfo.getName())
                .setText(R.id.tv_my_family_name, familyInfo.getName())
                .setText(R.id.tv_my_family_number, familyInfo.getNum() + "个设备");
        if (familyInfo.isDefault()) {
            baseViewHolder.setImageResource(R.id.iv_family_number_default, R.mipmap.sel);
        } else {
            baseViewHolder.setImageResource(R.id.iv_family_number_default, R.mipmap.sel_no);
        }
    }
}
