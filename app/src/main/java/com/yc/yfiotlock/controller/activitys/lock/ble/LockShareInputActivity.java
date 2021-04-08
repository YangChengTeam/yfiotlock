package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.ShareDeviceEngine;
import com.yc.yfiotlock.model.engin.UserEngine;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class LockShareInputActivity extends BaseBackActivity {


    @BindView(R.id.view_line)
    View mViewLine;
    @BindView(R.id.tv_sure)
    TextView mTvSure;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @BindView(R.id.et_account)
    EditText mEtAccount;
    @BindView(R.id.tv_device_name)
    TextView mTvDeviceName;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_lock_share_input;
    }

    DeviceInfo deviceInfo;

    @Override
    protected void initViews() {
        super.initViews();
        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("deviceInfo");
        backNavBar.setTitle(deviceInfo.getName().concat("共享管理"));
        mTvDeviceName.setText(deviceInfo.getName());
        mEtAccount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getUserInfo();
            }
            return false;
        });
        mEtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 11) {
                    mTvSure.setClickable(true);
                    mTvSure.setBackgroundResource(R.drawable.fast_login_btn_bg);
                } else {
                    mTvSure.setClickable(false);
                    mTvSure.setBackgroundResource(R.drawable.fast_login_press);
                }
            }
        });
        mTvSure.setClickable(false);
        mEtAccount.requestFocus();
    }

    @Override
    protected void bindClick() {
        setClick(mTvSure, this::getUserInfo);
    }

    @Override
    protected void initVars() {
        super.initVars();
        mUserEngine = new UserEngine(getContext());
    }

    private UserEngine mUserEngine;

    private void getUserInfo() {
        mLoadingDialog.show("分享中...");
        String msg = "分享失败";
        mUserEngine.getUserInfo(mEtAccount.getText().toString()).subscribe(new Observer<ResultInfo<UserInfo>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), msg);
            }

            @Override
            public void onNext(ResultInfo<UserInfo> info) {
                if (info.getCode() == 1) {
                    mLoadingDialog.dismiss();
                    LockShareCommitActivity.start(getContext(), deviceInfo, info.getData());
                } else {
                    String tmsg = msg;
                    tmsg = info != null && info.getMsg() != null ? info.getMsg() : tmsg;
                    ToastCompat.show(getContext(), tmsg);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(String s) {
        if (s.equals(ShareDeviceEngine.SHARE_DEVICE_SUCCESS)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserEngine != null) {
            mUserEngine.cancelAll();
        }
    }
}