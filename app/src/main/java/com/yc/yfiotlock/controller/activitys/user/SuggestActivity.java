package com.yc.yfiotlock.controller.activitys.user;

import android.os.Bundle;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuggestActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_suggest;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
    }


    @OnClick(R.id.stv_commit)
    public void onViewClicked() {
    }
}