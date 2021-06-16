package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class FingerprintAddSelectHandOpenLockActivity extends BaseFingerprintAddOpenLockActivity {
    @BindView(R.id.iv_left_hand)
    View leftHandBtn;

    @BindView(R.id.iv_right_hand)
    View rightHandBtn;

    private int keyid;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_select_hand_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        keyid = getIntent().getIntExtra("keyid", 0);
        ToastCompat.show(getContext(), "添加成功");
    }

    @Override
    protected void initViews() {
        super.initViews();

        RxView.clicks(leftHandBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2last(leftHandBtn, FingerprintAddSelectHandNextOpenLockActivity.class);
        });

        RxView.clicks(rightHandBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2last(rightHandBtn, FingerprintAddSelectRightHandNextOpenLockActivity.class);
        });
    }

    private void nav2last(View view, Class clazz) {
        Intent intent = new Intent(FingerprintAddSelectHandOpenLockActivity.this, clazz);
        intent.putExtra("name", view.getTag() + "");
        intent.putExtra("keyid", keyid);
        startActivity(intent);
        finish();
    }
}
