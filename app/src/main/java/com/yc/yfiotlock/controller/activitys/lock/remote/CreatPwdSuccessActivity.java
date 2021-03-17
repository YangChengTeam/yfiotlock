package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Context;
import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;

public class CreatPwdSuccessActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;

    public static void start(Context context, PassWordInfo passWordInfo) {
        Intent intent = new Intent(context, CreatPwdSuccessActivity.class);
        intent.putExtra("password_info", passWordInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_creat_pwd_success;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

    }
}
