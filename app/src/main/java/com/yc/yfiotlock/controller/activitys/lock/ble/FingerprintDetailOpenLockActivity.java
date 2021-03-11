package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.ble.LockBLEData;

public class FingerprintDetailOpenLockActivity extends BaseDetailOpenLockActivity {
    @Override
    protected void initViews() {
        setTitle("指纹");
        super.initViews();

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(FingerprintDetailOpenLockActivity.this, FingerprintModifyOpenLockActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void bleDelSucc() {

    }

    @Override
    protected void cloudDelSucc() {

    }

    @Override
    protected void processData(LockBLEData bleData) {

    }
}
