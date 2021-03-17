package com.yc.yfiotlock.controller.dialogs.user;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.user.LoginActivity;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.adapters.SignCodeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class LoginDialog extends Dialog {
    @BindView(R.id.iv_cancel)
    ImageView mIvCancel;
    @BindView(R.id.et_sms_code)
    EditText mEtSmsCode;
    @BindView(R.id.tv_sent_code)
    TextView mTvSentCode;
    @BindView(R.id.rv_code)
    RecyclerView mRvCode;
    @BindView(R.id.tv_timer)
    TextView mTvTimer;

    LoginActivity mLoginActivity;

    public LoginDialog(LoginActivity context) {
        super(context);
        mLoginActivity = context;
        View view = LayoutInflater.from(context).inflate(
                getLayoutId(), null);
        ButterKnife.bind(this, view);
        setContentView(view);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    protected int getLayoutId() {
        return R.layout.user_dialog_login;
    }


    protected void initViews() {
        setCanceledOnTouchOutside(false);
        if (CacheUtil.getCache(Config.LOGIN_SEND_CODE_URL + phone, long.class) != null) {
            setSendSmsTvText(Config.LOGIN_SEND_CODE_URL + phone, mTvTimer);
        }
        mEtSmsCode.setOnLongClickListener(v -> true);
        showInput();
        setRvCode();
        mEtSmsCode.addTextChangedListener(mTextWatcher);
    }

    SignCodeAdapter mSignCodeAdapter;

    private void setRvCode() {
        mSignCodeAdapter = new SignCodeAdapter(null);
        mSignCodeAdapter.setOnItemClickListener((adapter, view, position) -> {
            showInput();
        });
        mRvCode.setAdapter(mSignCodeAdapter);
        mRvCode.setLayoutManager(new GridLayoutManager(getContext(), Config.SMS_CODE_LENGTH, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        List<String> strings1 = new ArrayList<>();
        for (int i = 0; i < Config.SMS_CODE_LENGTH; i++) {
            strings1.add("");
        }
        mSignCodeAdapter.setNewInstance(strings1);
    }

    public void show(String phoneNumber) {
        phone = phoneNumber;
        show();
        mEtSmsCode.setText("");
        mEtSmsCode.setOnLongClickListener(v -> true);
        mTvSentCode.setText("验证码已发送至".concat("+").concat(phoneNumber));
        setSendSmsTvText(Config.LOGIN_SEND_CODE_URL + phone, mTvTimer);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            char[] chars = s.toString().toCharArray();
            List<String> stringList = mSignCodeAdapter.getData();
            for (int i = 0; i < Config.SMS_CODE_LENGTH; i++) {
                if (i < chars.length) {
                    stringList.set(i, chars[i] + "");
                } else {
                    stringList.set(i, "");
                }
                mSignCodeAdapter.notifyItemChanged(i, "");
            }
            if (chars.length == Config.SMS_CODE_LENGTH) {
                mLoginResult.onSuccess(s.toString(), phone);
                dismiss();
            }
        }
    };

    private String phone;


    public void setSendSmsCodeCache() {
        CacheUtil.setSendCodeTime(Config.LOGIN_SEND_CODE_URL + phone, System.currentTimeMillis());
        setSendSmsTvText(Config.LOGIN_SEND_CODE_URL + phone, mTvTimer);
    }


    public void showInput() {
        mEtSmsCode.setFocusable(true);
        mEtSmsCode.setFocusableInTouchMode(true);
        mEtSmsCode.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEtSmsCode, InputMethodManager.SHOW_IMPLICIT);
    }


    /**
     * @param url      发送验证码的接口地址
     * @param textView 发送验证码的textview
     */
    public void setSendSmsTvText(String url, TextView textView) {
        if (CommonUtil.findActivity(getContext()).isDestroyed()) {  //如果结束了 就停止递归 避免内存泄漏
            return;
        }
        //设置不可点击 就不能请求发送验证码
        textView.setClickable(false);
        //获取上一次的发送时间
        long sendTime = CacheUtil.getSendCodeTime(url);
        if (sendTime == 0) { //如果发送时间为0 说明从未发送过验证码
            CacheUtil.setSendCodeTime(url, System.currentTimeMillis()); // 保存一下当前时间作为上一次发送验证码的时间
            textView.postDelayed(() -> setSendSmsTvText(url, textView), 1000);//递归设置UI显示
            return;
        }
        if (System.currentTimeMillis() - sendTime > 60000) {  //如果当前时间距离上次发送时间大于60s 就可以再次发送
            textView.setText("重新发送");
            textView.setTextColor(Color.parseColor("#3395FD"));
            textView.setClickable(true);
            return;
        }

        //  当前时间和发送时间小于60s 则 更新UI时间 不改变TV的可点击状态
        int remainingTime = 60 - (int) (System.currentTimeMillis() - sendTime) / 1000;

        String countString = "<font color=\"#3395FD\">" + remainingTime + "</font>"
                + "<font color=\"#9a9a9a\">秒后可重新发送</font>";

        textView.setText(Html.fromHtml(countString));
        textView.postDelayed(() -> setSendSmsTvText(url, textView), 1000);
    }


    private LoginResult mLoginResult;

    public void setLoginResult(LoginResult loginResult) {
        mLoginResult = loginResult;
    }

    @OnClick({R.id.iv_cancel, R.id.et_sms_code, R.id.tv_timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                dismiss();
                break;
            case R.id.tv_timer:
                mLoginResult.onSendSmsCode();
                break;

        }
    }

    public interface LoginResult {
        void onSuccess(String code, String phone);

        void onSendSmsCode();
    }
}
