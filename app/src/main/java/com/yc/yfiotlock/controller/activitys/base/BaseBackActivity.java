package com.yc.yfiotlock.controller.activitys.base;

import android.view.View;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;

public abstract class BaseBackActivity extends BaseActivity {
    @BindView(R.id.view_nav_bar)
    BackNavBar backNavBar;

    @Override
    protected void initViews() {
        backNavBar.setBackListener(new BackNavBar.BackListener() {
            @Override
            public void onBack(View view) {
                finish();
            }
        });
    }
}
