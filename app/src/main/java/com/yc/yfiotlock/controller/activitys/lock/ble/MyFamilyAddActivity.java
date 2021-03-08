package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.TempPwdDetailActivity;
import com.yc.yfiotlock.model.bean.FamilyInfo;
import com.yc.yfiotlock.model.bean.PassWordInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.RightNextTextView;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MyFamilyAddActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_family_add_name)
    RightNextTextView tvName;
    @BindView(R.id.tv_family_add_location)
    RightNextTextView tvLocation;
    @BindView(R.id.tv_family_add_address)
    RightNextTextView tvAddress;

    private FamilyInfo familyInfo;

    public static void start(Context context, FamilyInfo familyInfo) {
        Intent intent = new Intent(context, MyFamilyAddActivity.class);
        intent.putExtra("family_info", familyInfo);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_my_family_add;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        initData();

        RxView.clicks(tvName).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyNameActivity.start(MyFamilyAddActivity.this, familyInfo);
        });
        RxView.clicks(tvLocation).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyLocationActivity.start(MyFamilyAddActivity.this, familyInfo);
        });
        RxView.clicks(tvAddress).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyAddressActivity.start(MyFamilyAddActivity.this, familyInfo);
        });
    }

    private void initData() {
        Serializable serializable = getIntent().getSerializableExtra("family_info");
        if (serializable instanceof FamilyInfo) {
            FamilyInfo familyInfo = (FamilyInfo) serializable;
            this.familyInfo = familyInfo;
            tvName.setTvDes(familyInfo.getName(), Color.parseColor("#000000"));
            tvLocation.setTvDes(familyInfo.getLocation(), Color.parseColor("#000000"));
            tvAddress.setTvDes(familyInfo.getHomAddress(), Color.parseColor("#000000"));

            mBnbTitle.setTitle(familyInfo.getName());
        }
    }
}
