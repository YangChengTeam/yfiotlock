package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.constant.Config;

import java.util.concurrent.TimeUnit;

public class CardOpenLockActivity extends BaseOpenLockActivity {

    @Override
    protected void initViews() {
        setTitle("NFC门卡");
        super.initViews();

        RxView.clicks(addTv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            Intent intent = new Intent(CardOpenLockActivity.this, CardAddOpenLockActivity.class);
            startActivity(intent);
        });

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(CardOpenLockActivity.this, CardDetailOpenLockActivity.class);
                startActivity(intent);
            }
        });
    }
}
