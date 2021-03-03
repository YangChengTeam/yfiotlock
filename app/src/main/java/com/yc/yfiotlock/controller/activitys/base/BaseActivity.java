package com.yc.yfiotlock.controller.activitys.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.helper.PermissionHelper;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private PermissionHelper mPermissionHelper;

    public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(getLayoutId());
        setTranslucentStatus();
        ButterKnife.bind(this);
        mPermissionHelper = new PermissionHelper();
        initViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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


}
