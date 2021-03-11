package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtils;

public class CardDetailOpenLockActivity extends BaseDetailOpenLockActivity {
    @Override
    protected void initViews() {
        setTitle("NDF门卡");
        super.initViews();

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(CardDetailOpenLockActivity.this, CardModifyOpenLockActivity.class);
                intent.putExtra("openlockinfo", openLockInfo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void bleDel() {

    }

    @Override
    protected void cloudDelSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setPasswordCount(countInfo.getCardCount() - 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }

    @Override
    protected void processData(LockBLEData bleData) {

    }
}
