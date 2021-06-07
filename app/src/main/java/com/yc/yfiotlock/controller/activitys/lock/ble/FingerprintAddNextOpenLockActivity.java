package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEBaseCmd;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtil;

import java.util.Arrays;

import butterknife.BindView;

public class FingerprintAddNextOpenLockActivity extends BaseFingerprintAddOpenLockActivity {

    @BindView(R.id.tv_op_result)
    TextView resultTv;
    @BindView(R.id.iv_tip)
    ImageView fpIv;

    private String name = "未命名指纹";
    private int keyid = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_next_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        number = getIntent().getStringExtra("number");
    }

    @Override
    protected void initViews() {
        super.initViews();
        setTitle("指纹");
        resultTv.setText("1/6 录入成功");
    }

    @Override
    public void onBackPressed() {
        if (!lockBleSender.isOpOver()) {
            bleCancelDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData != null && lockBLEData.getMcmd() == LockBLEEventCmd.MCMD && lockBLEData.getScmd() == LockBLEEventCmd.SCMD_FINGERPRINT_INPUT_COUNT && lockBLEData.getExtra()[0] > (byte) 0x01) {
            int n = lockBLEData.getExtra()[0];
            resultTv.setText(n + "/6 录入成功");
            fpIv.setImageResource(getResources().getIdentifier("fp" + lockBLEData.getExtra()[0], "mipmap", this.getPackageName()));
        } else if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                if (lockBLEData.getExtra() != null) {
                    String number = new String(Arrays.copyOfRange(lockBLEData.getExtra(), 0, 8));
                    if (number.equals(this.number)) {
                        keyid = lockBLEData.getExtra()[8];
                        localAdd(keyid);
                    } else {
                        ToastCompat.show(getContext(), "流水号匹配不成功");
                    }
                }
            }
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        super.onNotifyFailure(lockBLEData);
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            ToastCompat.show(getContext(), "指纹添加失败");
            finish();
        }
    }

    @Override
    protected void localAdd(int keyid) {
        int fingerprintCount = 0;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            fingerprintCount = countInfo.getFingerprintCount();
        }
        fingerprintCount += 1;
        name += fingerprintCount;
        localAdd(name, LockBLEManager.OPEN_LOCK_FINGERPRINT, keyid, "");
    }

    @Override
    protected void localAddSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setFingerprintCount(countInfo.getFingerprintCount() + 1);
            CacheUtil.setCache(key, countInfo);
        }

        Intent intent = new Intent(getContext(), FingerprintAddSelectHandOpenLockActivity.class);
        intent.putExtra("keyid", keyid);
        startActivity(intent);
        finish();
    }

}
