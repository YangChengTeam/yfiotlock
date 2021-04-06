package com.yc.yfiotlock.controller.activitys.base;

import android.Manifest;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.user.MainActivity;
import com.yc.yfiotlock.download.DownloadManager;
import com.yc.yfiotlock.helper.PermissionHelper;
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
        setFullScreenWithCutOutScreen();

        mPermissionHelper.setMustPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        mPermissionHelper.checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                navToMain();
            }

            @Override
            public void onRequestPermissionError() {
                VUiKit.postDelayed(1000, () -> {
                    navToMain = true;
                    if (SplashActivity.this.hasWindowFocus()) {
                        navToMain();
                    }
                });
            }
        });
    }

    /**
     * 加入这个判断是用来解决：若用户在开屏跳转中途返回桌面，还会跳转到主页的问题
     * 更加符合用户需求（暂时有别的事要处理），而不是用户返回桌面了，过一会又打开app了
     * 如果在跳转的时候返回了，就加个标识，当用户再次切换回app的时候再跳转
     */
    private boolean navToMain = false;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && navToMain) {
            navToMain();
        }
    }

    private void navToMain() {
        navToMain = false;
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

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onRequestPermissionsResult(this, resultCode);
    }
}