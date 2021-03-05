package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.PassWordInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LogAdapter extends BaseExtendAdapter<PassWordInfo> {

    public LogAdapter(@Nullable List data) {
        super(R.layout.item_log, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PassWordInfo passWordInfo) {
        baseViewHolder.setText(R.id.tv_temp_pwd_name, passWordInfo.getName())
                .setText(R.id.tv_temp_pwd_validity, passWordInfo.getValidity());


        int state = passWordInfo.getState();
        int textColor = getContext().getResources().getColor(R.color.grayBg);
        switch (state) {
            case 0:
                passWordInfo.setStateDes("待生效");
                textColor = getContext().getResources().getColor(R.color.red_f84c3e);
                break;
            case 1:
                passWordInfo.setStateDes("已生效");
                break;
            case 2:
                passWordInfo.setStateDes("已失效");
                textColor = getContext().getResources().getColor(R.color.blue_2F90F7);
                break;
        }
        baseViewHolder.setText(R.id.tv_temp_pwd_state, passWordInfo.getStateDes())
                .setTextColor(R.id.tv_temp_pwd_state, textColor);
    }


}
