package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class FingerprintAddSelectHandOpenLockActivity extends BaseBackActivity {
    @BindView(R.id.iv_left_hand)
    View leftHandBtn;

    @BindView(R.id.iv_right_hand)
    View rightHandBtn;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_select_hand_open_lock;
    }

    @Override
    protected void initViews() {
        super.initViews();

        RxView.clicks(leftHandBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            Intent intent = new Intent(FingerprintAddSelectHandOpenLockActivity.this, FingerprintAddSelectHandNextOpenLockActivity.class);
            intent.putExtra("name", leftHandBtn.getTag() + "");
            startActivity(intent);
        });

        RxView.clicks(rightHandBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            Intent intent = new Intent(FingerprintAddSelectHandOpenLockActivity.this, FingerprintAddSelectHandNextOpenLockActivity.class);
            intent.putExtra("name", leftHandBtn.getTag() + "");
            startActivity(intent);
        });
    }
}
