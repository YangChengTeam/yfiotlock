package com.yc.yfiotlock;

import android.app.Application;
import android.os.Build;
import android.os.IBinder;
import android.os.IGpioService;

import androidx.room.Room;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.chad.library.adapter.base.module.LoadMoreModuleConfig;
import com.coorchice.library.ImageEngine;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.kk.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.dao.AppDatabase;
import com.yc.yfiotlock.helper.Reflection;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.helper.GlideHelper;
import com.yc.yfiotlock.model.engin.UpdateEngine;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.widgets.CustomLoadMoreView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class App extends Application {
    // 应用单例
    private static App app;

    public static App getApp() {
        return app;
    }

    // 所有云端设备
    private List<String> macList = new ArrayList<>();

    public List<String> getMacList() {
        return macList;
    }

    // 锁数据库
    private AppDatabase db;

    public AppDatabase getDb() {
        return db;
    }

    public static boolean isLogin() {
        return UserInfoCache.getUserInfo() != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Reflection.unseal(this);
        app = this;
        LockBLEManager.getInstance().initBle(this);
        initSdk();
        initHttp();
        initCommonConfig();
        initBauduMap();
        checkUpdate();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "lock").build();

        Method method = null;
        try {
            method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{"gpio"});
            IGpioService gpioService = IGpioService.Stub.asInterface(binder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private UpdateInfo mUpdateInfo;
    public UpdateInfo getUpdateInfo() {
        return mUpdateInfo;
    }
    private void checkUpdate() {
        UpdateEngine updateEngine = new UpdateEngine(this);
        updateEngine.getUpdateInfo().subscribe(resultInfo -> {
            if (resultInfo != null && resultInfo.getData() != null) {
                mUpdateInfo = resultInfo.getData().getUpgrade();
            }
        });
    }

    private void initBauduMap() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    private void initCommonConfig() {
        LoadMoreModuleConfig.setDefLoadMoreView(new CustomLoadMoreView());
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
        HttpConfig.setDefaultParams(params);

    }

    private void initSdk() {
        MMKV.initialize(this);
        ImageEngine.install(new GlideHelper(this));
        if (BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), "2efb5c9b77", true);
        } else {
            //for release
            CrashReport.initCrashReport(getApplicationContext(), "73c6b29460", false);
        }
    }


}
