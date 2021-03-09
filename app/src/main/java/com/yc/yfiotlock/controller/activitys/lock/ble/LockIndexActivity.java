package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

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

    @BindView(R.id.iv_loading)
    ImageView loadingIv;
    @BindView(R.id.iv_tap)
    View tabView;
    @BindView(R.id.iv_tap2)
    View tabView2;

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

        loading();
    }

    private void loading() {
        rotate(loadingIv);
        scale(tabView, 0.05f);
        scale(tabView2, 0.05f);
    }

    private void rotate(View view){
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(rotate);
    }

    private void scale(View view, float value) {
        ScaleAnimation scale = new ScaleAnimation(1f, 1.f + value, 1f, 1.f + value, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(3000);
        scale.setFillAfter(false);
        scale.setRepeatMode(ScaleAnimation.REVERSE);
        scale.setRepeatCount(ScaleAnimation.INFINITE);
        view.startAnimation(scale);
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

    private void nav2setting() {
        Intent intent = new Intent(this, LockSettingActivity.class);
        startActivity(intent);
    }
}
