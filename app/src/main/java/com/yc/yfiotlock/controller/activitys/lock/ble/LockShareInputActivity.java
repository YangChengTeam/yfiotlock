package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        mTvDeviceName.setText(deviceInfo.getName());
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

        setClick(mTvSure, () -> {
            LockShareCommitActivity.start(getContext(), deviceInfo, mEtAccount.getText().toString());
        });
    }

}