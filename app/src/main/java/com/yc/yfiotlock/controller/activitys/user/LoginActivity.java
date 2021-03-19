package com.yc.yfiotlock.controller.activitys.user;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.yc.yfiotlock.model.bean.user.LoginEvent;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.utils.UserInfoCache;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
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
        CommonUtil.startFastLogin(this);
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

    @Override
    protected void bindClick() {
        setClick(R.id.tv_get_code, this::sendSmsCode);
        setClick(R.id.tv_fast_login, () -> CommonUtil.startFastLogin(this));
        setClick(R.id.tv_user_agreement, () -> WebActivity.start(getContext(), Config.USER_AGREEMENT, getString(R.string.user_agreement)));
        setClick(R.id.tv_privacy_policy, () -> WebActivity.start(getContext(), Config.PRIVACY_POLICY, getString(R.string.privacy_policy)));
    }

    @Override
    protected void initVars() {
        super.initVars();
        mLoginEngin = new LoginEngin(getContext());
    }

    LoginDialog mLoginDialog;
    LoginEngin mLoginEngin;


    public void sendSmsCode() {
        if (mEtPhone.getText().length() != 11) {
            return;
        }
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

        long lastTime = CacheUtil.getSendCodeTime(Config.LOGIN_SEND_CODE_URL + mEtPhone.getText().toString());
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
                    startActivity(new Intent(getContext(), MainActivity.class));
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
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOneKeyLogin(LoginEvent event) {
        Log.i("aaaa", "onOneKeyLogin: " + event);
        switch (event.getStateString()) {
            case FAILED:
            case EVOKE_SUCCESS:
                mLoadingDialog.dismiss();
                break;
            case WAITING:
                mLoadingDialog.show("正在开启一键登录...");
                break;
            case CHECKING:
                mLoadingDialog.show("检查环境中...");
                break;
            default:
                break;
        }
    }

}
