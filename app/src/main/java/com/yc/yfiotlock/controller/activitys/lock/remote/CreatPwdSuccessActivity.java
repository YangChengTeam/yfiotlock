package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class CreatPwdSuccessActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.ll_creat_success_number)
    LinearLayout llPwds;
    @BindView(R.id.tv_creat_success_copy)
    TextView tvCopy;

    public static void start(Context context, PassWordInfo passWordInfo) {
        Intent intent = new Intent(context, CreatPwdSuccessActivity.class);
        intent.putExtra("password_info", passWordInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_creat_pwd_success;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        RxView.clicks(tvCopy).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {

        });

        Serializable passwordInfo = getIntent().getSerializableExtra("password_info");
        if (!(passwordInfo instanceof PassWordInfo)) {
            ToastUtil.toast2(this, "创建密码失败");
            finish();
            return;
        }
        String pwd = ((PassWordInfo) passwordInfo).getPwd().trim();
        if (TextUtils.isEmpty(pwd) || pwd.length() != 6) {
            ToastUtil.toast2(this, "创建密码失败");
            finish();
            return;
        }

        int childCount = llPwds.getChildCount();
        if (childCount != 6) {
            ToastUtil.toast2(this, "创建密码失败");
            finish();
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View childAt = llPwds.getChildAt(i);
            if (childAt instanceof TextView) {
                ((TextView) childAt).setText(pwd.substring(i, i + 1));
            }
        }
    }
}
