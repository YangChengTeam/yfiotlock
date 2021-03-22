package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.VUiKit;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.io.Serializable;

import butterknife.BindView;
import rx.Subscriber;

public class OpenLockActivty extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_open_hint)
    TextView tvHint;
    @BindView(R.id.cl_open_lock)
    ConstraintLayout clOpenLock;
    @BindView(R.id.cl_open_fail)
    ConstraintLayout clOpenFail;
    @BindView(R.id.tv_open_fail_des)
    TextView tvFailDes;


    private LockEngine lockEngine;

    public static void start(Context context, DeviceInfo deviceInfo) {
        Intent intent = new Intent(context, OpenLockActivty.class);
        intent.putExtra("device", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
    }


    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        Serializable device = getIntent().getSerializableExtra("device");
        if (device instanceof DeviceInfo) {
            open(((DeviceInfo) device).getId());
        }
    }

    private void open(String id) {
        if (TextUtils.isEmpty(id)) {
            ToastUtil.toast2(OpenLockActivty.this, "没找到门锁");
            return;
        }
        mLoadingDialog.show("开锁中...");
        lockEngine.longOpenLock(id).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();

                tvHint.setText("开锁指令下发失败");
            }

            @Override
            public void onNext(ResultInfo<String> resultInfo) {
                if (resultInfo != null) {
                    if (resultInfo.getCode() == 1) {
                        mLoadingDialog.setIcon(R.mipmap.icon_finish);
                        mLoadingDialog.show("开锁指令已下发");

                        VUiKit.postDelayed(1500, new Runnable() {
                            @Override
                            public void run() {
                                if (isDestroyed()) {
                                    return;
                                }
                                mLoadingDialog.dismiss();
                            }
                        });

                        String text = "开锁指令已下发<br>在门锁上输入" +
                                "<font color='#3395FD'>5#</font>" +
                                "后开启门锁";
                        tvHint.setText(Html.fromHtml(text));
                    } else {
                        mLoadingDialog.dismiss();
                        clOpenLock.setVisibility(View.GONE);
                        clOpenFail.setVisibility(View.VISIBLE);

                        tvFailDes.setText(resultInfo.getMsg());
                    }
                }
            }
        });
    }
}
