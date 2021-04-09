package com.yc.yfiotlock.controller.activitys.lock.ble;

import com.kk.securityhttp.domain.ResultInfo;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.controller.dialogs.lock.ble.AlarmOpenLockManagerDialog;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockCountRefreshEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.functions.Action1;

public class AlarmOpenLockManagerActivity extends OpenLockManagerActivity {
    private LockEngine lockEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_alarm_open_lock_manager;
    }

    @Override
    protected void initVars() {
        super.initVars();
        LockBLEManager.GROUP_TYPE = LockBLEManager.GROUP_HIJACK;

        lockEngine = new LockEngine(this);
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
    }

    private void setCountInfo() {
        int groupType = 2;
        String key = "locker_count_" + lockInfo.getId() + groupType;
        lockEngine.getOpenLockInfoCount(lockInfo.getId() + "", groupType + "").subscribe(new Action1<ResultInfo<OpenLockCountInfo>>() {
            @Override
            public void call(ResultInfo<OpenLockCountInfo> openLockCountInfoResultInfo) {
                if (openLockCountInfoResultInfo.getCode() == 1 && openLockCountInfoResultInfo.getData() != null) {
                    OpenLockCountInfo countInfo = openLockCountInfoResultInfo.getData();
                    CacheUtil.setCache(key, countInfo);
                    loadData();
                }
            }
        });
    }
}
