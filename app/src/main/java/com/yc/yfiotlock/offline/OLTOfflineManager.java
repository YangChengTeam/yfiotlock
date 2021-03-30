package com.yc.yfiotlock.offline;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
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

    public void saveOfflineData(String key, OpenLockInfo openLockInfo) {
        Log.d(TAG, "离线数据保存:" + key + openLockInfo.getKeyid());
        List<OpenLockInfo> openLockInfos = CacheUtil.getCache(key, new TypeReference<List<OpenLockInfo>>() {
        }.getType());
        if (openLockInfos != null) {
            for (OpenLockInfo topenLockInfo : openLockInfos) {
                if (topenLockInfo.getId().equals(openLockInfo.getId()) || openLockInfo.getKeyid() == topenLockInfo.getKeyid()) {
                    return;
                }
            }
        }
        if (openLockInfos == null) {
            openLockInfos = new ArrayList<>();
        }
        openLockInfos.add(openLockInfo);
        CacheUtil.setCache(key, openLockInfos);
    }

    public void delOfflineData(String key, OpenLockInfo openLockInfo) {
        Log.d(TAG, "离线数据删除:" + key + openLockInfo.getKeyid());
        List<OpenLockInfo> openLockInfos = CacheUtil.getCache(key, new TypeReference<List<OpenLockInfo>>() {
        }.getType());
        if (openLockInfos != null) {
            for (OpenLockInfo topenLockInfo : openLockInfos) {
                if (topenLockInfo.getId().equals(openLockInfo.getId()) || openLockInfo.getKeyid() == topenLockInfo.getKeyid()) {
                    openLockInfos.remove(topenLockInfo);
                    return;
                }
            }
        }
        CacheUtil.setCache(key, openLockInfos);
    }

    interface ExceCallbac {
        void exce(List<OpenLockInfo> lockInfos);
    }

    public void autoExceOfflineData(String type, List<OpenLockInfo> lockInfos, ExceCallbac callbac) {
        List<OpenLockInfo> lastLockInfos = null;
        if (lockInfos == null || lockInfos.size() == 0) {
            lastLockInfos = CacheUtil.getCache(type, new TypeReference<List<OpenLockInfo>>() {
            }.getType());
        } else {
            lastLockInfos = new ArrayList<>();
            List<OpenLockInfo> cacheLockInfos = CacheUtil.getCache(type, new TypeReference<List<OpenLockInfo>>() {
            }.getType());
            for (OpenLockInfo lockInfo : lastLockInfos) {
                boolean isExist = false;
                for (OpenLockInfo cacheLockInfo : cacheLockInfos) {
                    if (lockInfo.getKeyid() == cacheLockInfo.getKeyid()) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    lastLockInfos.add(lockInfo);
                }
            }
        }
        if (lastLockInfos != null && lastLockInfos.size() > 0 && callbac != null) {
            callbac.exce(lastLockInfos);
        }
    }

    public void autoAddOfflineData(String key, List<OpenLockInfo> lockInfos) {
        autoExceOfflineData(key, lockInfos, (lastLockInfos) -> {
            loopAdd(key, lastLockInfos, 0);
        });
    }

    public void autoDelOfflineData(String key, List<OpenLockInfo> lockInfos) {
        autoExceOfflineData(key, lockInfos, (lastLockInfos) -> {
            loopDel(key, lastLockInfos, 0);
        });
    }


    private void loopAdd(String key, List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() == 0) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.addOpenLockWay(openLockInfo.getLockId(), openLockInfo.getName(), openLockInfo.getKeyid() + "", openLockInfo.getType(), openLockInfo.getGroupType() + "", openLockInfo.getPassword()).subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        delOfflineData(key, openLockInfo);
                        loopAdd(key, openLockInfos, n + 1);
                        Log.d(TAG, "离线同步添加数据:" + key + openLockInfo.getKeyid());
                    }
                }
            });
        }
    }

    private void loopDel(String key, List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() == 0) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.delOpenLockWay(openLockInfo.getId()).subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        delOfflineData(key, openLockInfo);
                        loopDel(key, openLockInfos, n + 1);
                        Log.d(TAG, "离线同步删除数据:" + key + openLockInfo.getKeyid());
                    }
                }
            });
        }
    }
}
