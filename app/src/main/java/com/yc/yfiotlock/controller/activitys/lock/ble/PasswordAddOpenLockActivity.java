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
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
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

    private String number;

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
            cloudAddPwd("1");
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

    private boolean opStatus = false;
    private void bleAddPwd() {
        opStatus = false;
        Random rand = new Random();
        number = rand.nextInt(100000000) + "";
        byte[] bytes = LockBLEOpCmd.addPwd(PasswordAddOpenLockActivity.this, (byte) 0x01, number, passEt.getText() + "", new byte[]{00, 00, 00, 00, 00, 00}, new byte[]{00, 00, 00, 00, 00, 00});
        EventBus.getDefault().post(bytes);
        VUiKit.postDelayed(Config.OP_TIMEOUT, new Runnable() {
            @Override
            public void run() {
                if (!opStatus) {
                    LockBLEData lockBLEData = new LockBLEData();
                    lockBLEData.setMcmd((byte) 0x02);
                    lockBLEData.setScmd((byte) 0x02);
                    lockBLEData.setStatus((byte) 0x06);
                    EventBus.getDefault().post(lockBLEData);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        opStatus = true;
        if (bleData != null && bleData.getMcmd() == (byte) 0x02 && bleData.getScmd() == (byte) 0x02) {
            if (bleData.getOther() != null) {
                String number = new String(Arrays.copyOfRange(bleData.getOther(), 0, 5));
                byte keyId = bleData.getOther()[6];
                if (this.number.equals(number)) { // 验证流水号
                    cloudAddPwd(keyId + "");
                }
            }
        }
    }

    private void cloudAddPwd(String keyid) {
        int passwordCount = 0;
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            passwordCount = countInfo.getPasswordCount();
        }
        passwordCount += 1;
        String name = "密码" + ((passwordCount) > 9 ? passwordCount + "" : "0" + passwordCount);
        cloudAdd(name, Config.OPEN_LOCK_PASSWORD, keyid, passEt.getText() + "");
    }

    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if(countInfo != null){
            countInfo.setPasswordCount(countInfo.getPasswordCount() + 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }


}
