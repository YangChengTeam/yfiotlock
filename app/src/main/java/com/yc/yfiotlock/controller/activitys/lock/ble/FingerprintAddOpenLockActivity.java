package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

public class FingerprintAddOpenLockActivity extends BaseAddOpenLockActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_open_lock;
    }


    @Override
    protected void initViews() {
        super.initViews();
        cloudAdd("左手无名指01", LockBLEManager.OPEN_LOCK_FINGERPRINT, "3", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == (byte) 0x08 && bleData.getScmd() == (byte) 0x01 && bleData.getStatus() == (byte) 0x01) {
            Intent intent = new Intent(FingerprintAddOpenLockActivity.this, FingerprintAddNextOpenLockActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if(countInfo != null){
            countInfo.setFingerprintCount(countInfo.getFingerprintCount() + 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }
}
