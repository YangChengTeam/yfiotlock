package com.yc.yfiotlock.helper;

import android.content.Context;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.eventbus.CloudAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeleteEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudUpdateEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.model.engin.LogEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public class CloudHelper {
    private OpenLockDao openLockDao;
    private LockEngine lockEngine;
    protected LogEngine logEngine;
    protected LockLogDao lockLogDao;

    public CloudHelper(Context context) {
        logEngine = new LogEngine(context);
        lockEngine = new LockEngine(context);
        openLockDao = App.getApp().getDb().openLockDao();
        lockLogDao = App.getApp().getDb().lockLogDao();
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

    protected void cloudAdd(int lockId, String name, int type, int keyid, String password) {
        lockEngine.addOpenLockWay(lockId + "", name, keyid + "", type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步添加: keyid:" + keyid + " lockid:" + lockId);
                    openLockDao.updateAddOpenLockInfo(lockId, keyid, true).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }


    protected void cloudDel(int lockId, int keyid) {
        lockEngine.delOpenLockWay2(lockId + "", keyid + "").subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步删除: keyid:" + keyid + " lockid:" + lockId);
                    openLockDao.realDeleteOpenLockInfo(lockId, keyid).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }

    protected void cloudEdit(int lockId, int keyid, String name) {
        lockEngine.modifyOpenLockName2(lockId + "", keyid + "", name).subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    LogUtil.msg("同步更新: keyid:" + keyid + " lockid:" + lockId);
                    openLockDao.updateOpenLockInfo(lockId, keyid).subscribeOn(Schedulers.io()).subscribe();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudAdd(CloudAddEvent cloudAddEvent) {
        OpenLockInfo openLockInfo = cloudAddEvent.getOpenLockInfo();
        cloudAdd(openLockInfo.getLockId(), openLockInfo.getName(), openLockInfo.getType(), openLockInfo.getKeyid(), openLockInfo.getPassword());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudDelete(CloudDeleteEvent cloudDeleteEvent) {
        OpenLockInfo openLockInfo = cloudDeleteEvent.getOpenLockInfo();
        cloudDel(openLockInfo.getLockId(), openLockInfo.getKeyid());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudEdit(CloudUpdateEvent cloudUpdateEvent) {
        OpenLockInfo openLockInfo = cloudUpdateEvent.getOpenLockInfo();
        cloudEdit(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudLogAdd(LogInfo logInfo) {
        cloudLogAdd(logInfo);
    }


}
