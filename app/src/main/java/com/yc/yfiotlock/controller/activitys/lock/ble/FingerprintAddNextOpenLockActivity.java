package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class FingerprintAddNextOpenLockActivity extends BaseFingerprintAddOpenLockActivity {

    @BindView(R.id.tv_op_result)
    TextView resultTv;
    @BindView(R.id.iv_tip)
    ImageView fpIv;

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
        resultTv.setText("1/6 录入成功");
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData != null && lockBLEData.getMcmd() == (byte) 0x08 && lockBLEData.getScmd() == (byte) 0x01 && lockBLEData.getStatus() > (byte) 0x01) {
            resultTv.setText(lockBLEData.getStatus() + "/6 录入成功");
            fpIv.setImageResource(getResources().getIdentifier("fp" + lockBLEData.getStatus(), "mipmap", this.getPackageName()));
        } else if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            if (lockBLEData.getStatus() == (byte) 0x00) {
                if (lockBLEData.getOther() != null) {
                    String number = new String(Arrays.copyOfRange(lockBLEData.getOther(), 0, 8));
                    if (number.equals(this.number)) {
                        int id = lockBLEData.getOther()[8];
                        Intent intent = new Intent(getContext(), FingerprintAddSelectHandOpenLockActivity.class);
                        intent.putExtra("keyid", id + "");
                        startActivity(intent);
                        finish();
                    } else {
                        ToastCompat.show(getContext(), "流水号匹配不成功");
                    }
                }
            }
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            ToastCompat.show(getContext(), "添加失败");
            finish();
        }
    }

}
