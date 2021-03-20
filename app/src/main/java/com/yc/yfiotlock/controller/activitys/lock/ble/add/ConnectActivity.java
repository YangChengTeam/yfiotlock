package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.utils.CommonUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;

public class ConnectActivity extends BaseAddActivity {
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.et_ssid)
    EditText mEtSsid;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_secret)
    ImageView mIvSecret;
    @BindView(R.id.stv_next)
    SuperTextView mStvNext;

    private BleDevice bleDevice;

    private static WeakReference<ConnectActivity> mInstance;
    public static void finish2(){
        if(mInstance != null && mInstance.get() != null){
            mInstance.get().finish();
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect;
    }

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
    }


    @Override
    protected void initViews() {
        mInstance = new WeakReference<ConnectActivity>(this);
        super.initViews();
        backNavBar.setTitle(bleDevice.getName());
        setInfo();
    }

    private void setInfo() {
        mEtSsid.setText(CommonUtil.getSsid(this));
    }

    private void nav2next() {
        String ssid = mEtSsid.getText().toString();
        String pwd = mEtPwd.getText().toString();
        if (TextUtils.isEmpty(ssid)) {
            ToastCompat.show(this, "wifi名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd) || pwd.length() < 8) {
            ToastCompat.show(this, "密码不能为空或小于8个字符");
            return;
        }
        Intent intent = new Intent(this, Connect2Activity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("ssid", ssid);
        intent.putExtra("pwd", pwd);
        startActivity(intent);
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mIvSecret, () -> CommonUtil.hiddenEditText(mEtPwd, mIvSecret));
        setClick(mStvNext, () -> {
            nav2next();
            finish();
        });
    }


}
