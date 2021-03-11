package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

public class PasswordDetailOpenLockActivity extends BaseDetailOpenLockActivity {
    @Override
    protected void initViews() {
        setTitle("密码");
        super.initViews();

        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(PasswordDetailOpenLockActivity.this, PasswordModifyOpenLockActivity.class);
                intent.putExtra("openlockinfo", openLockInfo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void bleDel() {
        opStatus = false;
        byte[] bytes = LockBLEOpCmd.delPwd(this, (byte) Config.GROUP_TYPE, (byte) openLockInfo.getKeyid());
        EventBus.getDefault().post(bytes);
        VUiKit.postDelayed(Config.OP_TIMEOUT, new Runnable() {
            @Override
            public void run() {
                if (!opStatus) {
                    LockBLEData lockBLEData = new LockBLEData();
                    lockBLEData.setMcmd((byte) 0x02);
                    lockBLEData.setScmd((byte) 0x03);
                    lockBLEData.setStatus((byte) 0x06);  //超时
                    EventBus.getDefault().post(lockBLEData);
                }
            }
        });
    }

    @Override
    protected void cloudDelSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setPasswordCount(countInfo.getPasswordCount() - 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }

    @Override
    protected void processData(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == (byte) 0x02 && bleData.getScmd() == (byte) 0x02) {
        }
    }
}
