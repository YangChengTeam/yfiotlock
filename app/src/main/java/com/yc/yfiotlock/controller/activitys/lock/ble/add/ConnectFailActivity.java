package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectFailActivity extends BaseBackActivity {

    @BindView(R.id.iv_pic)
    ImageView mIvPic;
    @BindView(R.id.ll_tip)
    LinearLayout mLlTip;
    @BindView(R.id.stv_reset)
    SuperTextView mStvReset;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_add_activity_connect_fail;
    }

    @Override
    protected void initViews() {
        super.initViews();
        String name = getIntent().getStringExtra("name") == null ? "" : getIntent().getStringExtra("name");
        backNavBar.setTitle(name);
        setClick(mStvReset, this::finish);
    }
}
