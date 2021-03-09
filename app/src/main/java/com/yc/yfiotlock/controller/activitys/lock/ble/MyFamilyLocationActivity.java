package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.FamilyInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;

public class MyFamilyLocationActivity extends BaseActivity {

    public static void start(Context context, FamilyInfo familyInfo) {
        Intent intent = new Intent(context, MyFamilyLocationActivity.class);
        if (familyInfo != null) {
            intent.putExtra("family_info", familyInfo);
        }
        context.startActivity(intent);
    }

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_family_location;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
    }
}
