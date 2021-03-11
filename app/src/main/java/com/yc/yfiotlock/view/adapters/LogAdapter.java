package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.utils.DateUtils;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.LogInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LogAdapter extends BaseExtendAdapter<LogInfo> {
    public LogAdapter(@Nullable List data) {
        super(R.layout.item_log, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LogInfo logInfo) {
        baseViewHolder.setText(R.id.tv_log_name, logInfo.getAction_name())
                .setText(R.id.tv_log_des, logInfo.getDesp())
                .setText(R.id.tv_log_date, DateUtils.timestamp2Date(logInfo.getAction_time()))
                .setImageResource(R.id.iv_log_icon, logInfo.getIcon());
    }
}
