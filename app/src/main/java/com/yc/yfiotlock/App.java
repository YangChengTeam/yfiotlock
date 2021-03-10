package com.yc.yfiotlock;

import android.app.Application;
import android.os.Build;

import com.chad.library.adapter.base.module.LoadMoreModuleConfig;
import com.clj.fastble.BleManager;
import com.coorchice.library.ImageEngine;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.ble.LockBLEPackage;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.helper.Reflection;
import com.yc.yfiotlock.model.engin.GlideEngine;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.widgets.CustomLoadMoreView;

import java.util.HashMap;
import java.util.Map;


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
        Reflection.unseal(this);
        app = this;
        initBle();
        initSdk();
        initHttp();
        initCommonConfig();
    }

    private void initCommonConfig() {
        LoadMoreModuleConfig.setDefLoadMoreView(new CustomLoadMoreView());
    }

    private void initBle() {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 1000)
                .setSplitWriteNum(LockBLEPackage.getMtu())
                .setConnectOverTime(10000)
                .setOperateTimeout(5000).init(this);
    }

    private void initHttp() {
        GoagalInfo.get().init(getApplicationContext());
        HttpConfig.setPublickey(Config.PUBLIC_KEY);
        String agent_id = "1";
        Map<String, String> params = new HashMap<>();
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
            agent_id = GoagalInfo.get().channelInfo.agent_id;
        }

        params.put("agent_id", agent_id);
        params.put("ts", System.currentTimeMillis() + "");
        params.put("device_type", "2");
        params.put("imeil", GoagalInfo.get().uuid);
        String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
        params.put("sv", sv);

        if (GoagalInfo.get().getPackageInfo() != null) {
            params.put("av", GoagalInfo.get().getPackageInfo().versionCode + "");
        }
//        params.put("deviceid", getDeviceId(getApplicationContext()));
//        params.put("androidosv", Build.VERSION.SDK_INT + "");
//        params.put("msaoaid", oaid);
        HttpConfig.setDefaultParams(params);

    }

    private void initSdk() {
        MMKV.initialize(this);
        ImageEngine.install(new GlideEngine(this));
    }
}
