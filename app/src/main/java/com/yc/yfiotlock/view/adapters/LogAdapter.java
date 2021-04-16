package com.yc.yfiotlock.view.adapters;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.utils.DateUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
            case LockBLEManager
                    .OPEN_LOCK_FINGERPRINT:
                name = "指纹";
                break;
            case LockBLEManager
                    .OPEN_LOCK_PASSWORD:
                name = "密码";
                break;
            case LockBLEManager
                    .OPEN_LOCK_CARD:
                name = "NFC卡";
                break;
            case LockBLEEventCmd.SCMD_DOORBELL + 2:
                name = "门铃";
                break;
            case LockBLEEventCmd.SCMD_LOW_BATTERY + 2:
                name = ("低电报警");
                break;
            case LockBLEEventCmd.SCMD_LOCAL_INIT + 2:
                name = ("本地初始化");
                break;
            case LockBLEEventCmd.SCMD_LOCK_CLOSED + 2:
                name = ("门锁锁定");
                break;
            case LockBLEEventCmd.SCMD_LOCK_UNCLOSED + 2:
                name = ("门未锁好");
                break;
            case LockBLEEventCmd.SCMD_DOOR_UNCLOSED + 2:
                name = ("门未关上");
                break;
            case LockBLEEventCmd.SCMD_AVOID_PRY_ALARM + 2:
                name = ("防撬报警");
                break;
        }
        baseViewHolder.setText(R.id.tv_log_name, name);

        baseViewHolder.setGone(R.id.view_item_log_line, baseViewHolder.getLayoutPosition() == 0);
    }
}
