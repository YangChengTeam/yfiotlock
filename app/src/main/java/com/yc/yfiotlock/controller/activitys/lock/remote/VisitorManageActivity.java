package com.yc.yfiotlock.controller.activitys.lock.remote;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;

import butterknife.BindView;

public class VisitorManageActivity extends BaseActivity {

    @BindView(R.id.visitor_manage_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_manage;
    }

    @Override
    protected void initViews() {

        initRv();
    }

    private void initRv() {

    }
}
