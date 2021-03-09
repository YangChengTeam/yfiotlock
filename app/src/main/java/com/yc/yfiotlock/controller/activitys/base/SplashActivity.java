package com.yc.yfiotlock.controller.activitys.base;

import android.content.Intent;

import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.user.MainActivity;
import com.yc.yfiotlock.download.DownloadManager;
import com.yc.yfiotlock.model.bean.user.PhoneTokenInfo;
import com.yc.yfiotlock.utils.CommonUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        DownloadManager.init(new WeakReference<>(this));
        if (App.isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            CommonUtils.startLogin(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifyOpenSuccess(PhoneTokenInfo phoneTokenInfo) {
        if (phoneTokenInfo != null && phoneTokenInfo.getCode().equals("600001")) {
            finish();
        }
    }

}