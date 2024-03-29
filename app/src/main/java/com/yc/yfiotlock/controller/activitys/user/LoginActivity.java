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
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
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

    private LoginDialog mLoginDialog;
    private LoginEngin mLoginEngin;

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

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
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
        mLoginDialog = new LoginDialog(this);
    }


    public void sendSmsCode() {
        if (mEtPhone.getText().length() != 11) {
            return;
        }

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
        mLoadingDialog.show("登录中...");
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
                    mLoadingDialog.dismiss();
                    UserInfo userInfo = info.getData();
                    UserInfoCache.setUserInfo(userInfo);
                    EventBus.getDefault().post(userInfo);
                    EventBus.getDefault().post(new IndexRefreshEvent());
                    startActivity(new Intent(getContext(), MainActivity.class));
                    finish();
                } else {
                    String msg = info == null && info.getMsg() != null ? "登录失败" : info.getMsg();
                    ToastCompat.show(getContext(), msg);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOneKeyLogin(LoginEvent event) {
        switch (event.getStateString()) {
            case FAILED:
            case EVOKE_SUCCESS:
                mLoadingDialog.dismiss();
                break;
            case WAITING:
                mLoadingDialog.show("开启一键登录...");
                break;
            case CHECKING:
                mLoadingDialog.show("检查环境中...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginEngin != null) {
            mLoginEngin.cancelAll();
        }
    }
}
