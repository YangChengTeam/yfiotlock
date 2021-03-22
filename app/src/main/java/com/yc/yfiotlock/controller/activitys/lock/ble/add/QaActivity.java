package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

public class QaActivity  extends BaseBackActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_add_activity_qa;
    }

    @Override
    protected void initViews() {
            setClick(R.id.view_nav_bar, this::finish);
    }
}
