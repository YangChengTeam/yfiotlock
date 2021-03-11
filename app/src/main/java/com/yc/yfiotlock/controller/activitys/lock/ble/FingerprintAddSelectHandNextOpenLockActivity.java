package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.view.RxViewGroup;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;

public class FingerprintAddSelectHandNextOpenLockActivity extends BaseAddOpenLockActivity {

    @BindViews({R.id.iv_finger1, R.id.iv_finger2, R.id.iv_finger3, R.id.iv_finger4, R.id.iv_finger5})
    View[] fingerBtns;

    private String name;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_select_hand_next_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        name = getIntent().getStringExtra("name");
    }

    @Override
    protected void initViews() {
        super.initViews();
        for (int i = 0; i < fingerBtns.length; i++) {
            final View fingerBtn = fingerBtns[i];
            RxView.clicks(fingerBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                name += fingerBtn.getTag() + "";
                bleAddFingerprint();
            });
        }
    }

    private void bleAddFingerprint() {
        this.mcmd = (byte) 0x02;
        this.scmd = (byte) 0x08;
        byte[] bytes = LockBLEOpCmd.addFingerprint(this, LockBLEManager.GROUP_TYPE, number);
        lockBleSend.send(scmd, mcmd, bytes);
    }

    @Override
    protected void cloudAdd(String keyid) {
        int fingerprintCount = 0;
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            fingerprintCount = countInfo.getFingerprintCount();
        }
        fingerprintCount += 1;
        name += fingerprintCount;
        cloudAdd(name, LockBLEManager.OPEN_LOCK_FINGERPRINT, keyid, "");
    }

    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setPasswordCount(countInfo.getFingerprintCount() + 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }
}
