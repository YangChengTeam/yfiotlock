package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.utils.CommonUtil;

import butterknife.BindView;

public class ConnectActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.et_ssid)
    EditText mEtNameSsid;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_secret)
    ImageView mIvSecret;
    @BindView(R.id.stv_next)
    SuperTextView mStvNext;

    private BleDevice bleDevice;
    private LockBLESend lockBleSend;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect;
    }

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
        lockBleSend = new LockBLESend(this, bleDevice);

    }

    private void bindWifi() {
        if (lockBleSend != null) {
            String ssid = mEtNameSsid.getText().toString();
            String pwd = mEtPwd.getText().toString();
            lockBleSend.send((byte) 0x01, (byte) 0x02, LockBLESettingCmd.wiftDistributionNetwork(this, ssid, pwd));
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        backNavBar.setTitle(bleDevice.getName());
        setInfo();
    }

    private void setInfo() {
        mEtNameSsid.setText(CommonUtil.getSsid(this));
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mIvSecret, () -> CommonUtil.hiddenEditText(mEtPwd, mIvSecret));
        setClick(mStvNext, () -> {
            bindWifi();
        });
    }

    @Override
    public void onSuccess(byte[] data) {

    }

    @Override
    public void onFailure(byte status, String error) {

    }


}
