package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Context;
import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;

public class Connect2Activity extends BaseBackActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
    }

    public static void start(Context context, BleDevice bleDevice) {
        Intent intent = new Intent(context, Connect2Activity.class);
        intent.putExtra("bleDevice", bleDevice);
        context.startActivity(intent);
    }

    @Override
    protected void initViews() {
        super.initViews();
    }
}
