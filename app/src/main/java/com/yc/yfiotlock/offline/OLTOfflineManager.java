package com.yc.yfiotlock.offline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.utils.CacheUtil;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;


// OpenLockType 开锁方式 离线管理

public class OLTOfflineManager {
    public static final String TAG = "OfflineManager";
    private static OLTOfflineManager instance = null;

    private DeviceEngin deviceEngin;
    private LockEngine lockEngine;
    private LogEngine logEngine;

    private OpenLockDao openLockDao;
    private LockLogDao lockLogDao;
    private DeviceDao deviceDao;

    public static OLTOfflineManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OLTOfflineManager.class) {
                if (instance == null) {
                    instance = new OLTOfflineManager();
                    instance.lockEngine = new LockEngine(context);
                    instance.logEngine = new LogEngine(context);
                    instance.deviceEngin = new DeviceEngin(context);
                    instance.openLockDao = App.getApp().getDb().openLockDao();
                    instance.lockLogDao = App.getApp().getDb().lockLogDao();
                    instance.deviceDao = App.getApp().getDb().deviceDao();
                }
            }
        }
        return instance;
    }

    private void loopOpenLockAdd(List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() > n) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.addOpenLockWay(openLockInfo.getLockId() + "", openLockInfo.getName(), openLockInfo.getKeyid() + "", openLockInfo.getType(), openLockInfo.getGroupType() + "", openLockInfo.getPassword()).subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && (info.getCode() == 1 || info.getCode() == -101)) {
                        LogUtil.msg("同步添加开门方式: lockid:" + openLockInfo.getLockId() + " keyid:" + openLockInfo.getKeyid() + " group_type:" + openLockInfo.getGroupType());
                        openLockDao.updateAddOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType()).subscribeOn(Schedulers.io()).subscribe();
                        loopOpenLockAdd(openLockInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopOpenLockDel(List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() > n) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.delOpenLockWaySyncLocal(openLockInfo.getLockId() + "", openLockInfo.getKeyid() + "", openLockInfo.getGroupType() + "").subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步删除开门方式: lockid:" + openLockInfo.getLockId() + " keyid:" + openLockInfo.getKeyid() + " group_type:" + openLockInfo.getGroupType());
                        openLockDao.realDeleteOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType()).subscribeOn(Schedulers.io()).subscribe();
                        loopOpenLockDel(openLockInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopOpenLockEdit(List<OpenLockInfo> openLockInfos, int n) {
        if (openLockInfos != null && openLockInfos.size() > n) {
            OpenLockInfo openLockInfo = openLockInfos.get(n);
            if (openLockInfo == null) return;
            lockEngine.modifyOpenLockNameSyncLocal(openLockInfo.getLockId() + "", openLockInfo.getKeyid() + "", openLockInfo.getGroupType() + "", openLockInfo.getName()).subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步更新开门方式: lockid:" + openLockInfo.getLockId() + " keyid:" + openLockInfo.getKeyid() + " group_type:" + openLockInfo.getGroupType());
                        openLockDao.updateOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType()).subscribeOn(Schedulers.io()).subscribe();
                        loopOpenLockDel(openLockInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopLogAdd(List<LogInfo> logInfos, int n) {
        if (logInfos != null && logInfos.size() > n) {
            LogInfo logInfo = logInfos.get(n);
            if (logInfo == null) return;
            logEngine.addLog(logInfo.getLockId(), logInfo.getEventId(), logInfo.getKeyid(), logInfo.getType(), logInfo.getGroupType(), logInfo.getLogType(), logInfo.getTime()).subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步添加日志: lockid:" + logInfo.getLockId() + " eventid:" + logInfo.getEventId());
                        lockLogDao.updateAddLogInfo(logInfo.getLockId(), logInfo.getEventId(), true).subscribeOn(Schedulers.io()).subscribe();
                        loopLogAdd(logInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopDeviceAdd(List<DeviceInfo> deviceInfos, int n) {
        if (deviceInfos != null && deviceInfos.size() > n) {
            DeviceInfo deviceInfo = deviceInfos.get(n);
            if (deviceInfo == null) return;
            deviceEngin.addDeviceInfo(deviceInfo.getFamilyId() + "", deviceInfo.getName(), deviceInfo.getMacAddress(), deviceInfo.getDeviceId(), 0).subscribe(new Action1<ResultInfo<DeviceInfo>>() {
                @Override
                public void call(ResultInfo<DeviceInfo> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步添加设备: mac:" + deviceInfo.getMacAddress());
                        deviceDao.updateAddDeviceInfo(deviceInfo.getMacAddress()).subscribeOn(Schedulers.io()).subscribe();
                        loopDeviceAdd(deviceInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopDeviceDel(List<DeviceInfo> deviceInfos, int n) {
        if (deviceInfos != null && deviceInfos.size() > n) {
            DeviceInfo deviceInfo = deviceInfos.get(n);
            if (deviceInfo == null) return;
            deviceEngin.delDeviceInfoSyncLocal(deviceInfo.getMacAddress() + "").subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步删除设备: mac:" + deviceInfo.getMacAddress());
                        deviceDao.realDeleteDeviceInfo(deviceInfo.getMacAddress()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                openLockDao.deleteInfoByLockId(deviceInfo.getId()).subscribeOn(Schedulers.io()).subscribe();
                                lockLogDao.deleteInfoByLockId(deviceInfo.getId()).subscribeOn(Schedulers.io()).subscribe();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }
                        });
                        loopDeviceDel(deviceInfos, n + 1);
                    }
                }
            });
        }
    }

    private void loopDeviceEdit(List<DeviceInfo> deviceInfos, int n) {
        if (deviceInfos != null && deviceInfos.size() > n) {
            DeviceInfo deviceInfo = deviceInfos.get(n);
            if (deviceInfo == null) return;
            deviceEngin.updateDeviceInfoSyncLocal(deviceInfo.getMacAddress() + "", deviceInfo.getName(), "").subscribe(new Action1<ResultInfo<String>>() {
                @Override
                public void call(ResultInfo<String> info) {
                    if (info != null && info.getCode() == 1) {
                        LogUtil.msg("同步更新设备: mac:" + deviceInfo.getMacAddress());
                        deviceDao.updateDeviceInfo(deviceInfo.getMacAddress()).subscribeOn(Schedulers.io()).subscribe();
                        loopDeviceEdit(deviceInfos, n + 1);
                    }
                }
            });
        }
    }

    @SuppressLint("CheckResult")
    private void autoExceOfflineDatas() {
        deviceDao.loadNeedAddDeviceInfos().subscribeOn(Schedulers.io()).subscribe(new Consumer<List<DeviceInfo>>() {
            @Override
            public void accept(List<DeviceInfo> deviceInfos) throws Exception {
                loopDeviceAdd(deviceInfos, 0);
            }
        });

        deviceDao.loadNeedDelDeviceInfos().subscribeOn(Schedulers.io()).subscribe(new Consumer<List<DeviceInfo>>() {
            @Override
            public void accept(List<DeviceInfo> deviceInfos) throws Exception {
                loopDeviceDel(deviceInfos, 0);
            }
        });

        deviceDao.loadNeedUpdateDeviceInfos().subscribeOn(Schedulers.io()).subscribe(new Consumer<List<DeviceInfo>>() {
            @Override
            public void accept(List<DeviceInfo> deviceInfos) throws Exception {
                loopDeviceEdit(deviceInfos, 0);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void autoExceOfflineDatas(DeviceInfo deviceInfo) {
        lockLogDao.loadNeedAddLogInfos(deviceInfo.getId()).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<LogInfo>>() {
            @Override
            public void accept(List<LogInfo> logInfos) throws Exception {
                loopLogAdd(logInfos, 0);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void autoExceOfflineDatas(DeviceInfo deviceInfo, int type) {
        openLockDao.loadNeedAddOpenLockInfos(deviceInfo.getId(), type).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                loopOpenLockAdd(openLockInfos, 0);
            }
        });

        openLockDao.loadNeedDelOpenLockInfos(deviceInfo.getId(), type).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                loopOpenLockDel(openLockInfos, 0);
            }
        });

        openLockDao.loadNeedUpdateOpenLockInfos(deviceInfo.getId(), type).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                loopOpenLockEdit(openLockInfos, 0);
            }
        });
    }

    public void doTask() {
        Log.d(TAG, "开始执行任务");
        autoExceOfflineDatas();
        IndexInfo indexInfo = CacheUtil.getCache(Config.INDEX_DETAIL_URL, IndexInfo.class);
        List<DeviceInfo> deviceInfos = null;
        if (indexInfo != null && indexInfo.getDeviceInfos() != null) {
            deviceInfos = indexInfo.getDeviceInfos();
        }
        if (deviceInfos != null && deviceInfos.size() > 0) {
            for (DeviceInfo deviceInfo : deviceInfos) {
                if (!TextUtils.isEmpty(deviceInfo.getMacAddress())) {
                    autoExceOfflineDatas(deviceInfo);
                    autoExceOfflineDatas(deviceInfo, LockBLEManager.GROUP_ADMIN);
                    autoExceOfflineDatas(deviceInfo, LockBLEManager.GROUP_HIJACK);
                }
            }
        }
    }
}
