package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.remote.PasswordInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TempPwdAdapter extends BaseExtendAdapter<PasswordInfo> implements LoadMoreModule {

    public TempPwdAdapter(@Nullable List data) {
        super(R.layout.item_temp_pwd, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PasswordInfo passWordInfo) {
        baseViewHolder.setText(R.id.tv_temp_pwd_name, passWordInfo.getName())
                .setText(R.id.tv_temp_pwd_validity, "有效期：一次使用");


        int state = passWordInfo.getTemporaryPwdStatus();
        int textColor = getContext().getResources().getColor(R.color.grayBg);
        switch (state) {
            case 1:
                passWordInfo.setStateDes("待生效");
                textColor = getContext().getResources().getColor(R.color.red_f84c3e);
                break;
            case 2:
                passWordInfo.setStateDes("已生效");
                textColor = getContext().getResources().getColor(R.color.blue_2F90F7);
                break;
            case 3:
                passWordInfo.setStateDes("已失效");
                textColor = getContext().getResources().getColor(R.color.gray_999);
                break;
        }
        baseViewHolder.setText(R.id.tv_temp_pwd_state, passWordInfo.getStateDes())
                .setTextColor(R.id.tv_temp_pwd_state, textColor);

        baseViewHolder.setGone(R.id.view_line, baseViewHolder.getAdapterPosition() == 0);
    }


}
