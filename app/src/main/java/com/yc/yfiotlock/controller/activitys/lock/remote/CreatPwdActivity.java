package com.yc.yfiotlock.controller.activitys.lock.remote;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;

public class CreatPwdActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_creat_pwd;
    }

    @Override
    protected void initViews() {

        mBnbTitle.setBackListener(view -> onBackPressed());

    }
}
