package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

public class PasswordOpenLockActivity extends BaseOpenLockActivity {
    @Override
    protected void initViews() {
        setTitle("密码");
        super.initViews();

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(PasswordOpenLockActivity.this, PasswordDetailOpenLockActivity.class);
                startActivity(intent);
            }
        });
    }
}
