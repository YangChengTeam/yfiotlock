package com.yc.yfiotlock;

import android.app.Application;

import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.utils.UserInfoCache;


public class App extends Application {
    private static App app;

    public static App getApp() {
        return app;
    }

    public static boolean isLogin() {
        return UserInfoCache.getUserInfo() != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initSdk();
    }

    private void initSdk() {
        MMKV.initialize(this);
    }
}
