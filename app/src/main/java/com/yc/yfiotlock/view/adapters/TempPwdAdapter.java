package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.NextTextInfo;
import com.yc.yfiotlock.model.bean.PassWordInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TempPwdAdapter extends BaseExtendAdapter<PassWordInfo> {

    public TempPwdAdapter(@Nullable List data) {
        super(R.layout.item_temp_pwd, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PassWordInfo nextTextInfo) {
        baseViewHolder.setText(R.id.tv_temp_pwd_name, nextTextInfo.getName())
                .setText(R.id.tv_temp_pwd_state, nextTextInfo.getStateDes())
                .setText(R.id.tv_temp_pwd_validity, nextTextInfo.getValidity());
    }


}
