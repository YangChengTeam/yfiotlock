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
                .setText(R.id.tv_log_date, logInfo.getTime());

        if (logInfo.getLogType() == 1) {
            baseViewHolder.setImageResource(R.id.iv_log_icon, R.mipmap.icon_log);
        } else {
            baseViewHolder.setImageResource(R.id.iv_log_icon, R.mipmap.alarm);
        }

        String name = "";
        int type = logInfo.getType();
        switch (type) {
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

        baseViewHolder.setText(R.id.tv_log_name, name);

        baseViewHolder.setGone(R.id.view_item_log_line, baseViewHolder.getLayoutPosition() == 0);
    }
}
