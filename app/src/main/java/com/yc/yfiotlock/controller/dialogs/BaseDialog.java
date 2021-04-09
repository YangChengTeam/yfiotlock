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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.eventbus.EventStub;
import com.yc.yfiotlock.utils.CommonUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        super(context, R.style.DialogTheme);

        View view = LayoutInflater.from(context).inflate(
                getLayoutId(), null);
        ButterKnife.bind(this, view);
        setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = ScreenUtil.getWidth(context);
        layoutParams.height = ScreenUtil.getHeight(context);
        view.setLayoutParams(layoutParams);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        bindClick();
    }

    public void bindClick() {}

    public void setClick(@IdRes int id, @NonNull Runnable runnable) {
        RxView.clicks(findViewById(id)).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view1 -> {
            runnable.run();
        });
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
        if (this.isShowing() && !CommonUtil.isActivityDestory(getContext())) {
            super.dismiss();
        }
    }
}
