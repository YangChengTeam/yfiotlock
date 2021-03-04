package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;

import butterknife.BindView;

public class StatusBar extends BaseView {
    @BindView(R.id.view_stub)
    View mStubView;

    public StatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        int statusBarHeight = ScreenUtil.dip2px(context, 12);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        mStubView.getLayoutParams().height = statusBarHeight;
        mStubView.setLayoutParams(mStubView.getLayoutParams());
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_status_bar;
    }
}
