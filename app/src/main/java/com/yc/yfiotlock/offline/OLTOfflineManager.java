package com.yc.yfiotlock.offline;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;


// OpenLockType 开锁方式 离线管理

public class OLTOfflineManager {
    public static final String TAG = "OfflineManager";
    private static OLTOfflineManager instance = null;

    protected LockEngine lockEngine;
    protected OpenLockDao openLockDao;

    public static OLTOfflineManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OLTOfflineManager.class) {
                if (instance == null) {
                    instance = new OLTOfflineManager();
                    instance.lockEngine = new LockEngine(context);
                    instance.openLockDao = App.getApp().getDb().openLockDao();
                }
            }
        }
        return instance;
    }

    private void loopAdd(List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() > n) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.addOpenLockWay(openLockInfo.getLockId() + "", openLockInfo.getName(), openLockInfo.getKeyid() + "", openLockInfo.getType(), openLockInfo.getGroupType() + "", openLockInfo.getPassword()).subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && (info.getCode() == 1 || info.getCode() == -101)) {
                        LogUtil.msg("同步添加: keyid:" + openLockInfo.getKeyid() + " lockid:" + openLockInfo.getLockId());
                        openLockDao.updateOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), true).subscribeOn(Schedulers.io()).subscribe();
                        loopAdd(openLockInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopDel(List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() > n) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.delOpenLockWay(openLockInfo.getId() + "").subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步删除: keyid:" + openLockInfo.getKeyid() + " lockid:" + openLockInfo.getLockId());
                        openLockDao.deleteOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid());
                        loopDel(openLockInfos, n + 1);
                    }
                }
            });
        }
    }

    private void autoExceOfflineDatas(DeviceInfo deviceInfo, int type) {
        openLockDao.loadNeedAddOpenLockInfos(deviceInfo.getId(), type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                loopAdd(openLockInfos, 0);
            }
        });

        openLockDao.loadNeedDelOpenLockInfos(deviceInfo.getId(), type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                loopDel(openLockInfos, 0);
            }
        });
    }

    public void doTask() {
        Log.d(TAG, "开始执行任务");
        IndexInfo indexInfo = CacheUtil.getCache(Config.INDEX_DETAIL_URL, IndexInfo.class);
        List<DeviceInfo> deviceInfos = null;
        if (indexInfo != null && indexInfo.getDeviceInfos() != null) {
            deviceInfos = indexInfo.getDeviceInfos();
        }
        if (deviceInfos != null && deviceInfos.size() > 0) {
            for (DeviceInfo deviceInfo : deviceInfos) {
                if (!TextUtils.isEmpty(deviceInfo.getMacAddress())) {
                    autoExceOfflineDatas(deviceInfo, LockBLEManager.GROUP_ADMIN);
                    autoExceOfflineDatas(deviceInfo, LockBLEManager.GROUP_HIJACK);
                }
            }
        }
    }
}
