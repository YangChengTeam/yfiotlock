package com.yc.yfiotlock.controller.activitys.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.UserInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.stv_fast_login)
    SuperTextView mStvFastLogin;
    @BindView(R.id.stv_other)
    SuperTextView mStvOther;
    @BindView(R.id.tv_user_agreement)
    TextView mTvUserAgreement;
    @BindView(R.id.tv_privacy_policy)
    TextView mTvPrivacyPolicy;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_login;
    }

    @Override
    protected void initViews() {
        setFullScreen();
    }


    @OnClick({R.id.stv_fast_login, R.id.stv_other, R.id.tv_user_agreement, R.id.tv_privacy_policy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_fast_login:
                UserInfo userInfo = new UserInfo();
                userInfo.setAccount("66666666");
                userInfo.setDeviceNumber("10");
                userInfo.setName("彪哥");
                userInfo.setNickName("阿彪");
                userInfo.setFace("http://p.6ll.com/Upload/Picture/face/2021/601cbd15d323a.jpg");
                UserInfoCache.setUserInfo(userInfo);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.stv_other:
                break;
            case R.id.tv_user_agreement:
                break;
            case R.id.tv_privacy_policy:
                break;
        }
    }
}
