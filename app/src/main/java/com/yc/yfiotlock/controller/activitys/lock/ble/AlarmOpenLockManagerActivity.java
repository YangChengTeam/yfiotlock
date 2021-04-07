package com.yc.yfiotlock.controller.activitys.lock.ble;

import com.kk.securityhttp.domain.ResultInfo;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.dialogs.lock.ble.AlarmOpenLockManagerDialog;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.functions.Action1;

public class AlarmOpenLockManagerActivity extends OpenLockManagerActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_alarm_open_lock_manager;
    }

    @Override
    protected void initVars() {
        super.initVars();
        LockBLEManager.GROUP_TYPE = LockBLEManager.GROUP_HIJACK;
    }

    @Override
    protected void initViews() {
        super.initViews();

        boolean isShow = MMKV.defaultMMKV().getBoolean("AlarmOpenLockManagerDialog", false);
        if (!isShow) {
            AlarmOpenLockManagerDialog dialog = new AlarmOpenLockManagerDialog(this);
            dialog.show();
            MMKV.defaultMMKV().putBoolean("AlarmOpenLockManagerDialog", true);
        }

        setCountInfo();
        loadData();
    }

    private void setCountInfo() {
        int groupType = 2;
        String key = "locker_count_" + lockInfo.getId() + groupType;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo == null) {
            countInfo = new OpenLockCountInfo();
            CacheUtil.setCache(key, countInfo);
        }
    }

}
