package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtil;

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
        this.mcmd = LockBLEOpCmd.MCMD;
        this.scmd = LockBLEOpCmd.SCMD_DEL_CARD;
        byte[] bytes = LockBLEOpCmd.delCard(this, (byte) LockBLEManager.GROUP_TYPE, (byte) openLockInfo.getKeyid());
        lockBleSend.send(mcmd, scmd, bytes, false);
    }

    @Override
    protected void cloudDelSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setCardCount(countInfo.getCardCount() - 1);
            CacheUtil.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
        }
    }
}
