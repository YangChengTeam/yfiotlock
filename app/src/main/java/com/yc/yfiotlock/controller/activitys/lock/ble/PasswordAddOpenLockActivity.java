package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class PasswordAddOpenLockActivity extends BaseAddOpenLockActivity {
    @BindView(R.id.iv_pass_show_status)
    ImageView statusIv;

    @BindView(R.id.stv_commit)
    SuperTextView commitBtn;
    @BindView(R.id.et_pass)
    EditText passEt;

    private boolean isNext;
    private String password;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_password_add_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        isNext = getIntent().getBooleanExtra("next", false);
        password = getIntent().getStringExtra("password");
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (passEt.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        RxView.clicks(commitBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            addPwd();
        });

        if (isNext) {
            commitBtn.setText("添加");
        }

        passEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addPwd();
                }
                return false;
            }
        });

        statusIv.setSelected(true);
        RxView.clicks(statusIv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (statusIv.isSelected()) {
                passEt.setTransformationMethod(null);
                statusIv.setImageResource(R.mipmap.secret_see);
                statusIv.setSelected(false);
                passEt.setSelection(passEt.getText().length());
            } else {
                passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                statusIv.setImageResource(R.mipmap.secret_hide);
                statusIv.setSelected(true);
                passEt.setSelection(passEt.getText().length());
            }
        });


    }

    private void addPwd() {
        if (passEt.length() != 6) {
            ToastUtil.toast2(getContext(), "密码必需是长度为6位数字");
            return;
        }
        if (isNext) {
            if (!passEt.getText().toString().equals(password)) {
                ToastUtil.toast2(getContext(), "两次密码不一致");
                return;
            }
            bleAddPwd();
        } else {
            nav2next();
        }
    }

    private void nav2next() {
        Intent intent = new Intent(this, PasswordAddOpenLockActivity.class);
        intent.putExtra("next", true);
        intent.putExtra("password", passEt.getText().toString());
        startActivity(intent);
        finish();
    }

    private void bleAddPwd() {
        if (lockBleSend != null) {
            this.mcmd = (byte) 0x02;
            this.scmd = (byte) 0x02;
            byte[] bytes = LockBLEOpCmd.addPwd(this, LockBLEManager.GROUP_TYPE, number, passEt.getText() + "", new byte[]{00, 00, 00, 00, 00, 00}, new byte[]{00, 00, 00, 00, 00, 00});
            lockBleSend.send(mcmd, scmd, bytes, false);
        }
    }

    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setPasswordCount(countInfo.getPasswordCount() + 1);
            CacheUtil.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
        }
    }

    @Override
    protected void cloudAdd(String keyid) {
        int passwordCount = 0;
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            passwordCount = countInfo.getPasswordCount();
        }
        passwordCount += 1;
        String name = "密码" + ((passwordCount) > 9 ? passwordCount + "" : "0" + passwordCount);
        cloudAdd(name, LockBLEManager.OPEN_LOCK_PASSWORD, keyid, passEt.getText() + "");
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        super.onNotifyFailure(lockBLEData);
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            mLoadingDialog.dismiss();
            ToastCompat.show(getContext(), "密码已添加");
        }
    }

}
