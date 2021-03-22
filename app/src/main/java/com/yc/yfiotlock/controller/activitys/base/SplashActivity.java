package com.yc.yfiotlock.controller.activitys.base;

import android.content.Intent;

import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.user.MainActivity;
import com.yc.yfiotlock.download.DownloadManager;
import com.yc.yfiotlock.model.bean.user.PhoneTokenInfo;
import com.yc.yfiotlock.utils.CommonUtil;

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
        setFullScreen();
        VUiKit.postDelayed(1000,this::next);
    }

    private void next(){
        if (App.isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            CommonUtil.startLogin(this);
        }
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifyOpenSuccess(PhoneTokenInfo phoneTokenInfo) {
        if (phoneTokenInfo != null && phoneTokenInfo.getCode().equals("600001")) {
            finish();
        }
    }

}