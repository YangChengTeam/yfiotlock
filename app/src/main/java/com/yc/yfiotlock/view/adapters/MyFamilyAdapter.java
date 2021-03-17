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

        int aDefault = familyInfo.isDef();
        if (aDefault == 0) {
            baseViewHolder.setImageResource(R.id.iv_family_number_default, R.mipmap.sel);
        } else {
            baseViewHolder.setImageResource(R.id.iv_family_number_default, R.mipmap.sel_no);
        }
    }

    public void updateCheck(FamilyInfo familyInfo) {
        List<FamilyInfo> data = getData();
        int index = -1;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == familyInfo.getId()) {
                index = i;
            }
        }
        if (index >= 0) {
            for (int i = 0; i < data.size(); i++) {
                View viewByPosition = getViewByPosition(i, R.id.iv_family_number_default);
                if (index == i) {
                    data.get(i).setIsDef(0);
                    if (viewByPosition instanceof ImageView) {
                        ((ImageView) viewByPosition).setImageResource(R.mipmap.sel);
                    }
                } else {
                    data.get(i).setIsDef(1);
                    if (viewByPosition instanceof ImageView) {
                        ((ImageView) viewByPosition).setImageResource(R.mipmap.sel_no);
                    }
                }
            }
        }

    }
}
