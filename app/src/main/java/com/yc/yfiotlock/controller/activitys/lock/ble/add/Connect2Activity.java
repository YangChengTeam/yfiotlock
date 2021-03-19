package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Context;
import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;

public class Connect2Activity extends BaseBackActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect2;
    }

    public static void start(Context context, LockInfo lockInfo) {
        Intent intent = new Intent(context, Connect2Activity.class);
        intent.putExtra("info", lockInfo);
        context.startActivity(intent);
    }

    @Override
    protected void initViews() {
        super.initViews();
        LockInfo lockInfo = (LockInfo) getIntent().getSerializableExtra("info");
        if (lockInfo==null){
            ToastCompat.show(getContext(),"设备信息缺失，请重新选择");
            finish();
            return;
        }
        backNavBar.setTitle(lockInfo.getName());
    }
}
