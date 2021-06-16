package com.yc.yfiotlock.controller.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;


import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

/*
 * Created by　Dullyoung on 2020/11/5
 */
public abstract class BaseBottomSheetDialog extends BottomSheetDialog {
    public BaseBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public BaseBottomSheetDialog(Context context, int style){
        super(context, style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initViews();
        bindClick();
    }
    protected abstract int getLayoutId();
    protected abstract void initViews();

    public void bindClick() {

    }

    public void setClick(@IdRes int id, @NonNull Runnable runnable) {
        RxView.clicks(findViewById(id)).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view1 -> {
            runnable.run();
        });
    }
}
