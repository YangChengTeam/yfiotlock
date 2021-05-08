package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;

public class FingerprintAddSelectHandNextOpenLockActivity extends BaseFingerprintAddOpenLockActivity {

    @BindViews({R.id.iv_finger1, R.id.iv_finger2, R.id.iv_finger3, R.id.iv_finger4, R.id.iv_finger5})
    View[] fingerBtns;

    @BindView(R.id.tv_hand)
    TextView nameTv;

    private String name;
    private int keyid;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_select_hand_next_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        name = getIntent().getStringExtra("name");
        keyid = getIntent().getIntExtra("keyid", 0);
    }

    @Override
    protected void initViews() {
        super.initViews();
        for (int i = 0; i < fingerBtns.length; i++) {
            final View fingerBtn = fingerBtns[i];
            RxView.clicks(fingerBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                name += fingerBtn.getTag() + "";
                localAdd(keyid);
            });
        }

        nameTv.setText(name);
    }


    @Override
    protected void localAdd(int keyid) {
        int fingerprintCount = 0;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            fingerprintCount = countInfo.getFingerprintCount();
        }
        fingerprintCount += 1;
        name += fingerprintCount;
        localAdd(name, LockBLEManager.OPEN_LOCK_FINGERPRINT, keyid, "");
    }

    @Override
    protected void localAddSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setFingerprintCount(countInfo.getFingerprintCount() + 1);
            CacheUtil.setCache(key, countInfo);
        }
    }

    @Override
    public void finish() {
        mLoadingDialog.setIcon(R.mipmap.icon_finish);
        mLoadingDialog.show("添加成功");
        VUiKit.postDelayed(1500, new Runnable() {
            @Override
            public void run() {
                if (CommonUtil.isActivityDestory(getContext())) {
                    return;
                }
                FingerprintAddSelectHandNextOpenLockActivity.super.finish();
            }
        });
    }
}
