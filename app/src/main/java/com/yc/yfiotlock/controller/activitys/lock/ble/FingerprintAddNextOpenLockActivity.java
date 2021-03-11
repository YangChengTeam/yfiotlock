package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class FingerprintAddNextOpenLockActivity extends BaseBackActivity {

    @BindView(R.id.tv_op_result)
    TextView resultTv;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_next_open_lock;
    }

    @Override
    protected void initViews() {
        super.initViews();
        resultTv.setText("1/6 录入成功");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == (byte) 0x08 && bleData.getScmd() == (byte) 0x01 && bleData.getStatus() > (byte) 0x01) {
            resultTv.setText(bleData.getStatus() + "/6 录入成功");
            if (bleData.getStatus() == (byte) 0x06) {
                Intent intent = new Intent(FingerprintAddNextOpenLockActivity.this, FingerprintAddSelectHandOpenLockActivity.class);
                startActivity(intent);
            }
        }
    }
}
