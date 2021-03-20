package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;

public abstract class BaseAddActivity extends BaseBackActivity {
    protected FamilyInfo familyInfo;

    @Override
    protected void initVars() {
        super.initVars();
        familyInfo = (FamilyInfo) getIntent().getSerializableExtra("family");
    }
}
