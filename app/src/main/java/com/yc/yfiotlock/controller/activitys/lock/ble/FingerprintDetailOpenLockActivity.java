package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.utils.CacheUtil;

public class FingerprintDetailOpenLockActivity extends BaseDetailOpenLockActivity {
    @Override
    protected void initViews() {
        setTitle("指纹");
        super.initViews();

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(FingerprintDetailOpenLockActivity.this, FingerprintModifyOpenLockActivity.class);
                intent.putExtra("openlockinfo", (OpenLockInfo) adapter.getData().get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void bleDel() {
        this.mcmd = LockBLEOpCmd.MCMD;
        this.scmd = LockBLEOpCmd.SCMD_DEL_PRINTFINGER;
        byte[] bytes = LockBLEOpCmd.delFingerprint(lockInfo.getKey(), (byte) LockBLEManager.GROUP_TYPE, (byte) openLockInfo.getKeyid());
        lockBleSender.send(mcmd, scmd, bytes);
    }

    @Override
    protected void localDelSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setFingerprintCount(countInfo.getFingerprintCount() - 1);
            CacheUtil.setCache(key, countInfo);
        }
    }


}
