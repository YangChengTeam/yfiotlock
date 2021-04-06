package com.yc.yfiotlock.offline;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;


// OpenLockType 开锁方式 离线管理

public class OLTOfflineManager {
    public static final String TAG = "OfflineManager";
    private static OLTOfflineManager instance = null;

    protected LockEngine lockEngine;

    public static OLTOfflineManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OLTOfflineManager.class) {
                if (instance == null) {
                    instance = new OLTOfflineManager();
                    instance.lockEngine = new LockEngine(context);
                }
            }
        }
        return instance;
    }



    interface ExceCallbac {
        void exce(List<OpenLockInfo> lockInfos);
    }

    public void autoAddOfflineData(String key, List<OpenLockInfo> openLockInfos, List<OpenLockInfo> cacheOpenLockInfos, ExceCallbac callback) {
        List<OpenLockInfo> lastOpenLockInfos = null;
        if (openLockInfos == null || openLockInfos.size() == 0) {
            lastOpenLockInfos = cacheOpenLockInfos;
        } else {
            lastOpenLockInfos = new ArrayList<>();
            for (OpenLockInfo cacheOpenLockInfo : cacheOpenLockInfos) {
                boolean isExist = false;
                for (OpenLockInfo openLockInfo : openLockInfos) {
                    if (openLockInfo.getKeyid() == cacheOpenLockInfo.getKeyid()) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    lastOpenLockInfos.add(cacheOpenLockInfo);
                }
            }
        }
        if (lastOpenLockInfos != null && lastOpenLockInfos.size() > 0 && callback != null) {
            Log.d(TAG, "离线同步数据:" + lastOpenLockInfos.size());
            callback.exce(lastOpenLockInfos);
        }
    }

    public void autoDelOfflineData(String key, List<OpenLockInfo> openLockInfos, List<OpenLockInfo> cacheOpenLockInfos, ExceCallbac callback) {
        List<OpenLockInfo> lastOpenLockInfos = null;
        if (openLockInfos == null || openLockInfos.size() == 0) {
            lastOpenLockInfos = cacheOpenLockInfos;
        } else {
            lastOpenLockInfos = new ArrayList<>();
            for (OpenLockInfo cacheOpenLockInfo : cacheOpenLockInfos) {
                boolean isExist = false;
                for (OpenLockInfo openLockInfo : openLockInfos) {
                    if (openLockInfo.getId() == cacheOpenLockInfo.getId()) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist) {
                    lastOpenLockInfos.add(cacheOpenLockInfo);
                }
            }
        }
        if (lastOpenLockInfos != null && lastOpenLockInfos.size() > 0 && callback != null) {
            Log.d(TAG, "离线同步数据:" + lastOpenLockInfos.size());
            callback.exce(lastOpenLockInfos);
        }
    }

    public void autoAddOfflineData(String key, List<OpenLockInfo> openLockInfos, List<OpenLockInfo> cacheLockInfos) {
        autoAddOfflineData(key, openLockInfos, cacheLockInfos, (lastLockInfos) -> {
            loopAdd(key, lastLockInfos, 0);
        });
    }

    public void autoDelOfflineData(String key, List<OpenLockInfo> openLockInfos, List<OpenLockInfo> cacheOpenLockInfos) {
        autoDelOfflineData(key, openLockInfos, cacheOpenLockInfos, (lastOpenLockInfos) -> {
            loopDel(key, lastOpenLockInfos, 0);
        });
    }


    private void loopAdd(String key, List<OpenLockInfo> openLockInfos, int n) {
        synchronized (OLTOfflineManager.class){
            if (openLockInfos != null && openLockInfos.size() > n) {
                OpenLockInfo openLockInfo = openLockInfos.get(n);
                if (openLockInfo == null) return;
                lockEngine.addOpenLockWay(openLockInfo.getLockId() + "", openLockInfo.getName(), openLockInfo.getKeyid() + "", openLockInfo.getType(), openLockInfo.getGroupType() + "", openLockInfo.getPassword()).subscribe(new Action1<ResultInfo<String>>() {
                    @Override
                    public void call(ResultInfo<String> info) {
                        if (info != null && info.getCode() == 1) {
                            Log.d(TAG, "添加离线同步数据:" + key + openLockInfo.getKeyid() + "-" + openLockInfo.getId());
                            loopAdd(key, openLockInfos, n + 1);
                        }
                    }
                });
            }
        }
    }

    private void loopDel(String key, List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() > n) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.delOpenLockWay(openLockInfo.getId()+"").subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        Log.d(TAG, "删除离线同步数据:" + key + openLockInfo.getKeyid() + "-" + openLockInfo.getId());
                        loopDel(key, openLockInfos, n + 1);
                    }
                }
            });
        }
    }

    private void autoExceOfflineDatas(DeviceInfo deviceInfo, int type) {
        String addkey = type + deviceInfo.getId() + "_add";
        String delkey = type + deviceInfo.getId() + "_del";

        List<OpenLockInfo> addCacheOpenLockInfos = CacheUtil.getCache(addkey, new TypeReference<List<OpenLockInfo>>() {
        }.getType());

        List<OpenLockInfo> delCacheOpenLockInfos = CacheUtil.getCache(delkey, new TypeReference<List<OpenLockInfo>>() {
        }.getType());

        if ((addCacheOpenLockInfos == null || addCacheOpenLockInfos.size() == 0) && (delCacheOpenLockInfos == null || delCacheOpenLockInfos.size() == 0)) {
            Log.d(TAG, "没有离线数据");
            return;
        }

        lockEngine.getOpenLockWayList(deviceInfo.getId() + "", "0", type + "").subscribe(new Action1<ResultInfo<List<OpenLockInfo>>>() {
            @Override
            public void call(ResultInfo<List<OpenLockInfo>> info) {
                synchronized (OLTOfflineManager.class){
                    if (info != null) {
                        List<OpenLockInfo> addCacheOpenLockInfos = CacheUtil.getCache(addkey, new TypeReference<List<OpenLockInfo>>() {
                        }.getType());
                        if ((addCacheOpenLockInfos != null && addCacheOpenLockInfos.size() > 0)) {
                            autoAddOfflineData(addkey, info.getData(), addCacheOpenLockInfos);
                        }

                        List<OpenLockInfo> delCacheOpenLockInfos = CacheUtil.getCache(delkey, new TypeReference<List<OpenLockInfo>>() {
                        }.getType());
                        if ((delCacheOpenLockInfos != null && delCacheOpenLockInfos.size() > 0)) {
                            autoDelOfflineData(delkey, info.getData(), delCacheOpenLockInfos);
                        }
                    }
                }
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
                if(!TextUtils.isEmpty(deviceInfo.getMacAddress())){
                    autoExceOfflineDatas(deviceInfo, LockBLEManager.GROUP_ADMIN);
                    autoExceOfflineDatas(deviceInfo, LockBLEManager.GROUP_HIJACK);
                }
            }
        }
    }
}
