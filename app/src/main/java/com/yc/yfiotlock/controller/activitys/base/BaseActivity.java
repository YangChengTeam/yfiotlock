package com.yc.yfiotlock.controller.activitys.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.user.LoginActivity;
import com.yc.yfiotlock.controller.activitys.user.WebActivity;
import com.yc.yfiotlock.controller.dialogs.LoadingDialog;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.eventbus.EventStub;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public abstract class BaseActivity extends BaseSendActivity implements ILoadData {

    protected PermissionHelper mPermissionHelper;

    public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(getLayoutId());
        setTranslucentStatus();
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mPermissionHelper = new PermissionHelper();
        initVars();
        initViews();
        bindClick();

    }

    public LoadingDialog mLoadingDialog;

    protected void initVars() {
    }

    /**
     * 设置view的点击事件
     * 最好结合{@link #setClick(int, Runnable)} 使用
     */
    protected void bindClick() {

    }

    /**
     * @param id       view id
     * @param runnable when click to do sth.
     */
    protected void setClick(@IdRes int id, @NonNull Runnable runnable) {
        RxView.clicks(findViewById(id)).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            runnable.run();
        });
    }

    /**
     * @param view     view
     * @param runnable when click to do sth.
     */
    protected void setClick(@NonNull View view, @NonNull Runnable runnable) {
        RxView.clicks(view).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view1 -> {
            runnable.run();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!App.isLogin()
                && this.getClass() != LoginActivity.class
                && this.getClass() != SplashActivity.class
                && this.getClass() != WebActivity.class) {
            CommonUtil.startLogin(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void stub(EventStub stub) {
    }

    @Override
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public Context getContext() {
        return this;
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(1280 | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * 刘海屏全屏
     */
    public void setFullScreenWithCutOutScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    public void success(Object data) {

    }

    @Override
    public void fail() {

    }

    @Override
    public void empty() {

    }


}
