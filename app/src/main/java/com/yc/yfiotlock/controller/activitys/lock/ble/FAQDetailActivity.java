package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.FAQInfo;

public class FAQDetailActivity extends BaseActivity {


    public static void start(Context context, FAQInfo FAQInfo) {
        Intent intent = new Intent(context, FAQDetailActivity.class);
        intent.putExtra("info", FAQInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_f_a_q_detail;
    }

    @Override
    protected void initViews() {

    }
}