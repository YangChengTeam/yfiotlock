package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.ButterKnife;


public abstract class BaseView extends ConstraintLayout {

    public BaseView(Context context){
        super(context);
        initViews(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    protected void initViews(Context context) {
        inflate(context, getLayoutId(), this);
        ButterKnife.bind(this);
    }

    public abstract int getLayoutId();

}
