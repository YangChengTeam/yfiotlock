package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;

import butterknife.BindView;

public class OpenLockActivty extends BaseActivity {
    @BindView(R.id.tv_open_hint)
    TextView tvHint;
    @BindView(R.id.cl_open_lock)
    ConstraintLayout clOpenLock;
    @BindView(R.id.cl_open_fail)
    ConstraintLayout clOpenFail;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_lock;
    }

    @Override
    protected void initViews() {
        String text = "开锁指令已下发 在门锁上输入" +
                "<font color='#3395FD'>4#</font>" +
                "后开启门锁";
        tvHint.setText(Html.fromHtml(text));

        VUiKit.postDelayed(3 * 1000, () -> {
            clOpenLock.setVisibility(View.GONE);
            clOpenFail.setVisibility(View.VISIBLE);
        });
    }
}
