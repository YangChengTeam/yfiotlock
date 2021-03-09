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
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.user.LoginDialog;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.LoginEngin;
import com.yc.yfiotlock.utils.CacheUtils;
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
        mTvGetCode.setClickable(false);
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

    @Override
    protected void initVars() {
        super.initVars();
        mLoginEngin = new LoginEngin(getContext());
    }

    LoginDialog mLoginDialog;
    LoginEngin mLoginEngin;


    public void sendSmsCode() {
        if (mLoginDialog == null) {
            mLoginDialog = new LoginDialog(this);
            mLoginDialog.setLoginResult(new LoginDialog.LoginResult() {
                @Override
                public void onSuccess(String code, String phone) {
                    onSmsCodeLogin(code, phone);
                }

                @Override
                public void onSendSmsCode() {
                    sendSmsCode();
                }
            });
        }

        long lastTime = CacheUtils.getSendCodeTime(Config.LOGIN_SEND_CODE_URL + mEtPhone.getText().toString());
        if (System.currentTimeMillis() - lastTime < 60000) {
            mLoginDialog.show(mEtPhone.getText().toString());
            return;
        }
        mLoadingDialog.show("发送中...");
        mLoginEngin.sendSmsCode(mEtPhone.getText().toString()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    mLoginDialog.show(mEtPhone.getText().toString());
                    mLoginDialog.setSendSmsCodeCache();
                } else {
                    ToastCompat.showCenter(getContext(), info == null ? "验证码发送失败" : info.getMsg());
                }
            }
        });
    }

    private void onSmsCodeLogin(String code, String phone) {

        mLoadingDialog.show("登陆中...");
        mLoginEngin.smsCodeLogin(phone, code).subscribe(new Observer<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), e.getMessage());
            }

            @Override
            public void onNext(ResultInfo<UserInfo> info) {
                if (info != null && info.getCode() == 1) {
                    UserInfoCache.setUserInfo(info.getData());
                    EventBus.getDefault().post(info.getData());
                } else {
                    ToastCompat.show(getContext(), info == null ? "登陆失败" : info.getMsg());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(UserInfo userInfo) {
        if (App.isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}
