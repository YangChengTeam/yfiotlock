package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.LockLogActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class LockIndexActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    View backBtn;
    @BindView(R.id.iv_setting)
    View settingBtn;

    @BindView(R.id.cd_open_lock)
    View openLockBtn;
    @BindView(R.id.cd_log)
    View logBtn;
    @BindView(R.id.cd_vm)
    View vmBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_index;
    }

    @Override
    protected void initViews() {
        setFullScreen();

        RxView.clicks(backBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            finish();
        });

        RxView.clicks(settingBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2setting();
        });

        RxView.clicks(openLockBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2OpenLock();
        });

        RxView.clicks(logBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2Log();
        });

        RxView.clicks(vmBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2Vm();
        });
    }

    private void nav2OpenLock() {
        Intent intent = new Intent(this, OpenLockManagerActivity.class);
        startActivity(intent);
    }

    private void nav2Vm() {
        Intent intent = new Intent(this, VisitorManageActivity.class);
        startActivity(intent);
    }

    private void nav2Log() {
        Intent intent = new Intent(this, LockLogActivity.class);
        startActivity(intent);
    }

    private void nav2setting(){
        Intent intent = new Intent(this, LockSettingActivity.class);
        startActivity(intent);
    }
}
