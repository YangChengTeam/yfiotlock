package com.yc.yfiotlock.controller.activitys.lock.remote;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;

import butterknife.BindView;

public class VisitorManageActivity extends BaseActivity {

    @BindView(R.id.cl_open_lock)
    ConstraintLayout mContentFL;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_manage;
    }

    @Override
    protected void initViews() {

    }
}
