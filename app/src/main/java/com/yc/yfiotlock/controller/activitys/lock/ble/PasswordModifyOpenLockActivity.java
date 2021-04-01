package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

public class PasswordModifyOpenLockActivity extends BaseModifyLockActivity {
    /**
     * @param context context
     * @param openLockInfo 需要id 和name
     */
    public static void start(Context context, OpenLockInfo openLockInfo) {
        Intent intent = new Intent(context, PasswordModifyOpenLockActivity.class);
        intent.putExtra("openlockinfo", openLockInfo);
        context.startActivity(intent);
    }
}
