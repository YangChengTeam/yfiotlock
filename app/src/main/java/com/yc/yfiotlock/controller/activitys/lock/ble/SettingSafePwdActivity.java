package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Switch;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Dullyoung
 */
public class SettingSafePwdActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @BindView(R.id.s_safe_pwd)
    Switch mSafePwd;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @BindView(R.id.s_fingerprint_unlock)
    Switch mFingerprintUnlock;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_setting_safe;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
    }


    @OnClick(R.id.tv_change_pwd)
    public void onViewClicked() {
    }
}