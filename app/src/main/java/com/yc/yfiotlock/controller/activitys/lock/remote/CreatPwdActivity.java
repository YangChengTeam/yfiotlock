package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.PassWordInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.io.Serializable;

import butterknife.BindView;

public class CreatPwdActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;

    public static void start(Context context, PassWordInfo passWordInfo) {
        Intent intent = new Intent(context, CreatPwdActivity.class);
        intent.putExtra("password_info", passWordInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_creat_pwd;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        VUiKit.postDelayed(3 * 1000, () -> {
            Serializable serializable = getIntent().getSerializableExtra("password_info");
            if (serializable instanceof PassWordInfo) {
                CreatPwdSuccessActivity.start(CreatPwdActivity.this, (PassWordInfo) serializable);
            }
        });

    }
}
