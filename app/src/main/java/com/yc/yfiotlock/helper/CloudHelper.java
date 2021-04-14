package com.yc.yfiotlock.helper;

import android.content.Context;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceDelEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceEditEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudOpenLockAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudOpenLockDeleteEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudOpenLockUpdateEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.model.engin.LogEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public class CloudHelper {

    private LockEngine lockEngine;
    private LogEngine logEngine;
    private DeviceEngin deviceEngin;

    private OpenLockDao openLockDao;
    private LockLogDao lockLogDao;
    private DeviceDao deviceDao;

    public CloudHelper(Context context) {
        logEngine = new LogEngine(context);
        lockEngine = new LockEngine(context);
        deviceEngin = new DeviceEngin(context);
        openLockDao = App.getApp().getDb().openLockDao();
        lockLogDao = App.getApp().getDb().lockLogDao();
        deviceDao = App.getApp().getDb().deviceDao();
    }

    public void registerNotify() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregisterNotify() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void cloudOpenLockAdd(OpenLockInfo openLockInfo) {
        lockEngine.addOpenLockWay(openLockInfo.getLockId() + "", openLockInfo.getName(), openLockInfo.getKeyid() + "", openLockInfo.getType(), openLockInfo.getGroupType() + "", openLockInfo.getPassword()).subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步添加开门方式: lockid:" + openLockInfo.getLockId() + " keyid:" + openLockInfo.getKeyid() + " group_type:" + openLockInfo.getGroupType());
                    openLockDao.updateAddOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType()).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }


    protected void cloudOpenLockDel(OpenLockInfo openLockInfo) {
        lockEngine.delOpenLockWaySyncLocal(openLockInfo.getLockId() + "", openLockInfo.getKeyid() + "", openLockInfo.getGroupType() + "").subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步删除开门方式: lockid:" + openLockInfo.getLockId() + " keyid:" + openLockInfo.getKeyid() + " group_type:" + openLockInfo.getGroupType());
                    openLockDao.realDeleteOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType()).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }

    protected void cloudOpenLockEdit(OpenLockInfo openLockInfo) {
        lockEngine.modifyOpenLockNameSyncLocal(openLockInfo.getLockId() + "", openLockInfo.getKeyid() + "", openLockInfo.getGroupType() + "", openLockInfo.getName()).subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步更新开门方式: lockid:" + openLockInfo.getLockId() + " keyid:" + openLockInfo.getKeyid() + " group_type:" + openLockInfo.getGroupType());
                    openLockDao.updateOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType()).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }

    protected void cloudLogAdd(LogInfo logInfo) {
        logEngine.addLog(logInfo.getLockId(), logInfo.getEventId(), logInfo.getKeyid(), logInfo.getType(), logInfo.getGroupType(), logInfo.getLogType(), logInfo.getTime()).subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步添加日志: lockid:" + logInfo.getLockId() + " eventid:" + logInfo.getEventId());
                    lockLogDao.updateAddLogInfo(logInfo.getLockId(), logInfo.getEventId(), true).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }

    protected void cloudDeviceAdd(DeviceInfo deviceInfo) {
        deviceEngin.addDeviceInfo(deviceInfo.getFamilyId() + "", deviceInfo.getName(), deviceInfo.getMacAddress(), deviceInfo.getDeviceId(), 0).subscribe(new Action1<ResultInfo<DeviceInfo>>() {
            @Override
            public void call(ResultInfo<DeviceInfo> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步添加设备: mac:" + deviceInfo.getMacAddress());
                    deviceDao.updateAddDeviceInfo(deviceInfo.getMacAddress()).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }


    protected void cloudDeivceDel(DeviceInfo deviceInfo) {
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
                }
            }
        });
    }

    protected void cloudDeivceEdit(DeviceInfo deviceInfo) {
        deviceEngin.updateDeviceInfoSyncLocal(deviceInfo.getMacAddress() + "", deviceInfo.getName(), "").subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步更新设备: mac:" + deviceInfo.getMacAddress());
                    deviceDao.updateDeviceInfo(deviceInfo.getMacAddress()).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudOpenLockAdd(CloudOpenLockAddEvent cloudOpenLockAddEvent) {
        OpenLockInfo openLockInfo = cloudOpenLockAddEvent.getOpenLockInfo();
        cloudOpenLockAdd(openLockInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudOpenLockDel(CloudOpenLockDeleteEvent cloudOpenLockDeleteEvent) {
        OpenLockInfo openLockInfo = cloudOpenLockDeleteEvent.getOpenLockInfo();
        cloudOpenLockDel(openLockInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudOpenLockEdit(CloudOpenLockUpdateEvent cloudOpenLockUpdateEvent) {
        OpenLockInfo openLockInfo = cloudOpenLockUpdateEvent.getOpenLockInfo();
        cloudOpenLockEdit(openLockInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudLogAdd(LogInfo logInfo) {
        cloudLogAdd(logInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudDeviceAdd(CloudDeviceAddEvent cloudDeviceAddEvent) {
        cloudDeviceAdd(cloudDeviceAddEvent.getDeviceInfo());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudDeviceDel(CloudDeviceDelEvent cloudDeviceDelEvent) {
        cloudDeivceDel(cloudDeviceDelEvent.getDeviceInfo());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudDeviceEdit(CloudDeviceEditEvent cloudDeviceEditEvent) {
        cloudDeivceEdit(cloudDeviceEditEvent.getDeviceInfo());
    }
}
