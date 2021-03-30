package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockShareCommitActivity extends BaseBackActivity {


    @BindView(R.id.iv_face)
    ImageView mIvFace;
    @BindView(R.id.tv_account)
    TextView mTvAccount;
    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.tv_sure)
    TextView mTvSure;

    public static void start(Context context, DeviceInfo deviceInfo, String account) {
        Intent intent = new Intent(context, LockShareCommitActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("DeviceInfo", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_lock_share_commit;
    }

    @Override
    protected void initViews() {
        super.initViews();
        DeviceInfo lockInfo = (DeviceInfo) getIntent().getSerializableExtra("DeviceInfo");
        String text = "<font color='#999999'>允许控制</font><font color='#222222'>【" + lockInfo.getName() + "】</font>";
        mTvResult.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        mTvAccount.setText(getIntent().getStringExtra("account"));
    }

    @Override
    protected void bindClick() {
        setClick(mTvSure, () -> {
            finish();
        });
    }


}