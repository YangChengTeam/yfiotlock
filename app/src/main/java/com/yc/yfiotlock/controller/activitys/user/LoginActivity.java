package com.yc.yfiotlock.controller.activitys.user;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.user.LoginDialog;
import com.yc.yfiotlock.model.bean.UserInfo;
import com.yc.yfiotlock.model.engin.LoginEngin;
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.utils.UserInfoCache;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.tv_get_code)
    TextView mTvGetCode;
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
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 11) {
                    mTvGetCode.setClickable(true);
                    mTvGetCode.setBackgroundResource(R.drawable.fast_login_btn_bg);
                } else {
                    mTvGetCode.setClickable(false);
                    mTvGetCode.setBackgroundResource(R.drawable.fast_login_press);
                }
            }
        });
    }


    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTime > 2000) {
            ToastCompat.show(getContext(), "再按一次退出");
            lastTime = System.currentTimeMillis();
        } else {
            if (MainActivity.getInstance() != null && MainActivity.getInstance().get() != null) {
                MainActivity.getInstance().get().finish();
            }
            System.exit(0);
        }
    }


    @OnClick({R.id.tv_get_code, R.id.tv_user_agreement, R.id.tv_privacy_policy, R.id.tv_fast_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_get_code:
                sendSmsCode();
                break;
            case R.id.tv_fast_login:
                CommonUtils.isVerifyEnable(this);
                break;
            case R.id.tv_user_agreement:
                break;
            case R.id.tv_privacy_policy:
                break;
        }
    }

    LoginDialog mLoginDialog;

    public void sendSmsCode() {
        if (mLoginDialog == null) {
            mLoginDialog = new LoginDialog(this);
            mLoginDialog.setLoginResult(new LoginDialog.LoginResult() {
                @Override
                public void onSuccess() {
                    finish();
                }

                @Override
                public void onSendSmsCode() {
                    sendSmsCode();
                }
            });
        }
        LoginEngin engin = new LoginEngin(getContext());
        engin.sendSmsCode(mEtPhone.getText().toString()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    mLoginDialog.show(mEtPhone.getText().toString());
                    mLoginDialog.setSendSmsCodeCache();
                } else {
                    ToastCompat.showCenter(getContext(), info == null ? "验证码发送失败" : info.getMsg());
                    setLocalInfo();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(UserInfo userInfo){
        if (App.isLogin()){
            finish();
        }
    }

    private void setLocalInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("阿彪66666");
        userInfo.setNickName("阿彪6啊");
        userInfo.setDeviceNumber("88");
        userInfo.setFace("http://p.6ll.com/Upload/Picture/face/2021/601cbd15d323a.jpg");

        UserInfoCache.setUserInfo(userInfo);
        EventBus.getDefault().post(userInfo);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
