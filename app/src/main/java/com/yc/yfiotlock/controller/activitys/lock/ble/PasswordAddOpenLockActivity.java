package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class PasswordAddOpenLockActivity extends BaseAddOpenLockActivity {
    @BindView(R.id.iv_pass_show_status)
    ImageView statusIv;

    @BindView(R.id.stv_commit)
    View commitBtn;
    @BindView(R.id.et_pass)
    EditText passEt;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_password_add_open_lock;
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (passEt.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        RxView.clicks(commitBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (passEt.length() != 6) {
                ToastUtil.toast2(getContext(), "密码必需是长度为6位数字");
                return;
            }
            cloudAdd("1");
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

    private void bleAddPwd() {
        this.mcmd = (byte)0x02;
        this.scmd = (byte)0x01;
        byte[] bytes = LockBLEOpCmd.addPwd(this, LockBLEManager.GROUP_TYPE, number, passEt.getText() + "", new byte[]{00, 00, 00, 00, 00, 00}, new byte[]{00, 00, 00, 00, 00, 00});
        lockBleSend.send(mcmd, scmd, bytes);
    }

    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if(countInfo != null){
            countInfo.setPasswordCount(countInfo.getPasswordCount() + 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }

    @Override
    protected void cloudAdd(String keyid) {
        int passwordCount = 0;
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            passwordCount = countInfo.getPasswordCount();
        }
        passwordCount += 1;
        String name = "密码" + ((passwordCount) > 9 ? passwordCount + "" : "0" + passwordCount);
        cloudAdd(name, LockBLEManager.OPEN_LOCK_PASSWORD, keyid, passEt.getText() + "");
    }


}
