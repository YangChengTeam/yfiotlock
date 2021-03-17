package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.utils.DateUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LogAdapter extends BaseExtendAdapter<LogInfo> implements LoadMoreModule {
    public LogAdapter(@Nullable List data) {
        super(R.layout.item_log, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LogInfo logInfo) {
        baseViewHolder
                .setText(R.id.tv_log_des, logInfo.getName())
                .setText(R.id.tv_log_date, DateUtils.timestamp2Date(logInfo.getAddTime()))
                .setText(R.id.tv_log_date, DateUtil.timestamp2Date(logInfo.getAdd_time()))
                .setImageResource(R.id.iv_log_icon, logInfo.getIcon());

        String name = "";
        int pwdType = logInfo.getPwdType();
        switch (pwdType) {
            case 1:
                name += "指纹";
                break;
            case 2:
                name += "密码";
                break;
            case 3:
                name += "门禁";
                break;
        }
        name += "开锁";
        int isSucc = logInfo.getIsSucc();
        switch (isSucc) {
            case 0:
                name += "失败";
                break;
            case 1:
                name += "成功";
                break;
        }
        baseViewHolder.setText(R.id.tv_log_name, name);

        baseViewHolder.setGone(R.id.view_item_log_line, baseViewHolder.getLayoutPosition() == 0);
    }
}
