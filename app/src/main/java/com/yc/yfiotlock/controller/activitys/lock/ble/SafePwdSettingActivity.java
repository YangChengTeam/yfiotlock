package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.SafeUtils;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yc.yfiotlock.controller.fragments.lock.ble.IndexFragment.CHECK_PWD;

/**
 * @author Dullyoung
 */
public class SafePwdSettingActivity extends BaseActivity implements Switch.OnCheckedChangeListener {


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

    DeviceInfo mDeviceInfo;

    @Override
    protected void initViews() {
        mDeviceInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        mBnbTitle.setBackListener(view -> finish());
        setClick(R.id.tv_change_pwd, () -> startActivityForResult(new Intent(this, SafePwdCreateActivity.class), REQUEST_PWD_CODE));
        setSwitch();
    }

    private void setSwitch() {
        mSafePwd.setOnCheckedChangeListener(this);
        mFingerprintUnlock.setOnCheckedChangeListener(this);
        switch (SafeUtils.getSafePwdType(mDeviceInfo)) {
            case SafeUtils.NO_PASSWORD:
                mSafePwd.setChecked(false);
                mFingerprintUnlock.setChecked(false);
                break;
            case SafeUtils.PASSWORD_TYPE:
                mSafePwd.setChecked(true);
                mFingerprintUnlock.setChecked(false);
                break;
            case SafeUtils.FINGERPRINT_TYPE:
                mSafePwd.setChecked(false);
                mFingerprintUnlock.setChecked(true);
                break;
            default:
                break;
        }
    }

    private final int REQUEST_PWD_CODE = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PWD_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String pwd = data.getStringExtra("pwd");
                SafeUtils.setSafePwd(mDeviceInfo, pwd);
                ToastCompat.show(getContext(), "新密码设置成功");
            }
        }
        if (requestCode == CHECK_PWD) {
            if (resultCode == RESULT_OK && data != null
                    && SafeUtils.getSafePwd(mDeviceInfo).equals(data.getStringExtra("pwd"))) {
                mSafePwd.setChecked(true);
                setSaveType(SafeUtils.PASSWORD_TYPE);
            } else {
                ToastCompat.show(getContext(), "密码错误");
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) {
            return;
        }
        if (isChecked) {
            buttonView.setChecked(false);
        }
        switch (buttonView.getId()) {
            //指纹
            case R.id.s_fingerprint_unlock:
                if (isChecked) {
                    checkFinger();
                }
                break;
            //密码
            case R.id.s_safe_pwd:
                if (isChecked) {
                    SafePwdCreateActivity.startCheck(this);
                }
                break;
            default:
                break;
        }
        if (!mFingerprintUnlock.isChecked() && !mSafePwd.isChecked()) {
            SafeUtils.setSafePwdType(mDeviceInfo, SafeUtils.NO_PASSWORD);
        }
    }

    private void checkFinger() {
        SafeUtils.useFinger(this, new Callback<String>() {
            @Override
            public void onSuccess(String resultInfo) {
                mFingerprintUnlock.setChecked(true);
                setSaveType(SafeUtils.FINGERPRINT_TYPE);
            }

            @Override
            public void onFailure(Response response) {
                mFingerprintUnlock.setChecked(false);
            }
        });
    }


    private void setSaveType(@IntRange(from = 1, to = 2) int type) {
        if (type == SafeUtils.PASSWORD_TYPE) {
            SafeUtils.setSafePwdType(mDeviceInfo, SafeUtils.PASSWORD_TYPE);
            if (mFingerprintUnlock.isChecked()) {
                mFingerprintUnlock.setChecked(false);
            }
        } else {
            SafeUtils.setSafePwdType(mDeviceInfo, SafeUtils.FINGERPRINT_TYPE);
            if (mSafePwd.isChecked()) {
                mSafePwd.setChecked(false);
            }
        }

    }

}