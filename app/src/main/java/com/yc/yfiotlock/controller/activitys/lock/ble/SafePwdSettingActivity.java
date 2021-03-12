package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Dullyoung
 */
public class SafePwdSettingActivity extends BaseActivity {


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
        setClick(R.id.tv_change_pwd, () -> startActivityForResult(new Intent(this, SafePwdCreateActivity.class), REQUEST_PWD_CODE));
    }

    private final int REQUEST_PWD_CODE = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PWD_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ToastCompat.showCenter(getContext(), data.getStringExtra("pwd"));
            }
        }
    }
}