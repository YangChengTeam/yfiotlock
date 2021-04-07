package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockCountRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.utils.BleUtil;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

public class CardOpenLockActivity extends BaseOpenLockActivity {

    @Override
    protected void initViews() {
        title = "NFC门卡";
        super.initViews();

        RxView.clicks(addBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            Intent intent = new Intent(CardOpenLockActivity.this, CardAddOpenLockActivity.class);
            startActivity(intent);
        });

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(CardOpenLockActivity.this, CardDetailOpenLockActivity.class);
                intent.putExtra("openlockinfo", (OpenLockInfo) adapter.getData().get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void success(Object data) {
        super.success(data);
        setCountInfo();
    }

    private void setCountInfo() {
        String key = "locker_count_" + lockInfo.getId() + groupType;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setCardCount(openLockAdapter.getData().size());
            CacheUtil.setCache(key, countInfo);
        }
        EventBus.getDefault().post(new OpenLockCountRefreshEvent());
    }
}
