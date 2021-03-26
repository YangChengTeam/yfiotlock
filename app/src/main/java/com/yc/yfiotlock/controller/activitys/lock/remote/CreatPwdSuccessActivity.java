package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.remote.PasswordInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class CreatPwdSuccessActivity extends BaseBackActivity {

    @BindView(R.id.ll_creat_success_number)
    LinearLayout llPwds;
    @BindView(R.id.tv_creat_success_copy)
    TextView tvCopy;
    @BindView(R.id.stv_btn_share)
    SuperTextView stvShare;
    @BindView(R.id.stv_btn_finish)
    SuperTextView stvFinish;

    public static void start(Context context, PasswordInfo passWordInfo) {
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
        Serializable passwordInfo = getIntent().getSerializableExtra("password_info");
        if (!(passwordInfo instanceof PasswordInfo)) {
            ToastUtil.toast2(this, "创建密码失败");
            finish();
            return;
        }
        String pwd = ((PasswordInfo) passwordInfo).getPwd().trim();
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

        RxView.clicks(tvCopy).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ble", pwd);
            clipboard.setPrimaryClip(clip);
            ToastUtil.toast2(CreatPwdSuccessActivity.this, "复制成功");
        });
        RxView.clicks(stvShare).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            ToastUtil.toast2(CreatPwdSuccessActivity.this, "分享");
        });
        RxView.clicks(stvFinish).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            finish();
        });
    }
}
