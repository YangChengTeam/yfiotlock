package com.yc.yfiotlock.controller.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.eventbus.EventStub;
import com.yc.yfiotlock.utils.CommonUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public abstract class BaseDialog extends Dialog {


    public BaseDialog(Context context) {
        super(context, R.style.DialogTheme);

        View view = LayoutInflater.from(context).inflate(
                getLayoutId(), null);
        ButterKnife.bind(this, view);
        setContentView(view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        try {
            layoutParams.width = ScreenUtil.getWidth(context);
            layoutParams.height = ScreenUtil.getHeight(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.setLayoutParams(layoutParams);

        setCancelable(true);

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void stub(EventStub stub){

    }


    protected void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public BaseDialog(Context context, int style) {
        super(context, style);

        View view = LayoutInflater.from(context).inflate(
                getLayoutId(), null);
        ButterKnife.bind(this, view);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        try {
            layoutParams.width = ScreenUtil.getWidth(context);
            layoutParams.height = ScreenUtil.getHeight(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setLayoutParams(layoutParams);

        setCancelable(true);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    @Override
    public void show() {
        if (!this.isShowing() && !CommonUtil.isActivityDestory(getContext())) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (this.isShowing() && !CommonUtil.isActivityDestory(getContext())) {
            super.dismiss();
        }
    }
}
