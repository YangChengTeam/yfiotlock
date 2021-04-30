package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.utils.SafeUtil;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;

import static com.yc.yfiotlock.controller.activitys.lock.ble.SafePwdCreateActivity.CHECK_ORIGIN_PWD;
import static com.yc.yfiotlock.controller.activitys.lock.ble.SafePwdCreateActivity.CREATE_NEW_PWD;
import static com.yc.yfiotlock.controller.activitys.lock.ble.SafePwdCreateActivity.INPUT_NEW_PWD;
import static com.yc.yfiotlock.controller.activitys.lock.ble.SafePwdCreateActivity.INPUT_NEW_PWD_AGAIN;

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
    @BindView(R.id.ll_finger)
    LinearLayout mLLFinger;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_setting_safe;
    }

    DeviceInfo mDeviceInfo;

    @Override
    protected void initViews() {
        mDeviceInfo = LockIndexActivity.getInstance().getLockInfo();
        mBnbTitle.setBackListener(view -> finish());
        setClick(R.id.tv_change_pwd, () -> {
            if (SafeUtil.DEFAULT.equals(SafeUtil.getSafePwd(mDeviceInfo))) {
                SafePwdCreateActivity.createNewPwd(this);
            } else {
                SafePwdCreateActivity.checkOrigin(this);
            }
        });
        setSwitch();
    }

    private void setSwitch() {
        mSafePwd.setOnCheckedChangeListener(this);
        mFingerprintUnlock.setOnCheckedChangeListener(this);
        switch (SafeUtil.getSafePwdType(mDeviceInfo)) {
            case SafeUtil.NO_PASSWORD:
                mSafePwd.setChecked(false);
                mFingerprintUnlock.setChecked(false);
                mLLFinger.setVisibility(View.GONE);
                break;
            case SafeUtil.PASSWORD_TYPE:
                mSafePwd.setChecked(true);
                break;
            case SafeUtil.FINGERPRINT_TYPE:
                mSafePwd.setChecked(true);
                mFingerprintUnlock.setChecked(true);
                break;
            default:
                break;
        }
    }

    private String inputNewPwd = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INPUT_NEW_PWD) {
            if (resultCode == RESULT_OK
                    && data != null && data.getStringExtra("pwd") != null) {
                inputNewPwd = data.getStringExtra("pwd");
                SafePwdCreateActivity.inputNewAgain(this);
            }
        }

        if (requestCode == INPUT_NEW_PWD_AGAIN) {
            if (resultCode == RESULT_OK) {
                if (data != null && inputNewPwd.equals(data.getStringExtra("pwd"))) {
                    SafeUtil.setSafePwd(mDeviceInfo, data.getStringExtra("pwd"));
                    ToastCompat.show(getContext(), "密码修改成功");
                } else {
                    ToastCompat.show(getContext(), "两次密码不一致");
                }
            }
        }

        if (requestCode == CHECK_ORIGIN_PWD) {
            if (resultCode == RESULT_OK) {
                if (data != null && SafeUtil.getSafePwd(mDeviceInfo).equals(data.getStringExtra("pwd"))) {
                    SafeUtil.setSafePwd(mDeviceInfo, data.getStringExtra("pwd"));
                    SafePwdCreateActivity.inputNew(this);
                } else {
                    ToastCompat.show(getContext(), "密码错误");
                }
            }
        }

        if (CREATE_NEW_PWD == requestCode) {
            if (resultCode == RESULT_OK && data != null && data.getStringExtra("pwd") != null) {
                ToastCompat.show(getContext(), "密码创建成功");
                SafeUtil.setSafePwd(mDeviceInfo, data.getStringExtra("pwd"));
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //过滤掉代码引起的变化 仅处理用户手动点击的
        if (!buttonView.isPressed()) {
            return;
        }

        switch (buttonView.getId()) {
            //指纹
            case R.id.s_fingerprint_unlock:
                if (isChecked) {
                    //开启开关之前 先验证对应的安全方式，在安全方式验证成功的回调中再通过代码打开开关
                    buttonView.setChecked(false);
                    checkFinger();
                }
                break;
            //密码
            case R.id.s_safe_pwd:
                if (isChecked) {
                    if (SafeUtil.getSafePwd(mDeviceInfo).equals(SafeUtil.DEFAULT)) {
                        mSafePwd.setChecked(false);
                        SafePwdCreateActivity.createNewPwd(this);
                        return;
                    }
                    mLLFinger.setVisibility(View.VISIBLE);
                    setSafeType(SafeUtil.PASSWORD_TYPE);
                } else {
                    mFingerprintUnlock.setChecked(false);
                    mLLFinger.setVisibility(View.GONE);
                    SafeUtil.setSafePwdType(mDeviceInfo, SafeUtil.NO_PASSWORD);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 之前没有设置过密码 打开的时候应该是创建新密码
     */


    private void checkFinger() {
        SafeUtil.useFinger(this, new Callback<String>() {
            @Override
            public void onSuccess(String resultInfo) {
                mFingerprintUnlock.setChecked(true);
                setSafeType(SafeUtil.FINGERPRINT_TYPE);
            }

            @Override
            public void onFailure(Response response) {
                mFingerprintUnlock.setChecked(false);
            }
        });
    }


    private void setSafeType(@IntRange(from = 1, to = 2) int type) {
        //切换为密码验证 设置指纹开关为关闭
        if (type == SafeUtil.PASSWORD_TYPE) {
            SafeUtil.setSafePwdType(mDeviceInfo, SafeUtil.PASSWORD_TYPE);
        } else {
            SafeUtil.setSafePwdType(mDeviceInfo, SafeUtil.FINGERPRINT_TYPE);
        }

    }

}