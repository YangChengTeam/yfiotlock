package com.yc.yfiotlock.helper;

import android.content.Context;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.eventbus.CloudAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeleteEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public class CloudHelper {
    private OpenLockDao openLockDao;
    private LockEngine lockEngine;

    public CloudHelper(Context context){
        lockEngine = new LockEngine(context);
        openLockDao = App.getApp().getDb().openLockDao();
    }

    public void registerNotify(){
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    public void unregisterNotify(){
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    protected void cloudAdd(int lockid, String name, int type, int keyid, String password) {
        lockEngine.addOpenLockWay(lockid + "", name, keyid + "", type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Action1<ResultInfo<String>>() {
            @Override
            public void call(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    openLockDao.updateOpenLockInfo(lockid, keyid, true).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });
    }


    protected void cloudDel(int id) {
        lockEngine.delOpenLockWay(id + "").subscribe();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloudAdd(CloudAddEvent cloudAddEvent) {
        OpenLockInfo openLockInfo = cloudAddEvent.getOpenLockInfo();
        cloudAdd(openLockInfo.getLockId(), openLockInfo.getName(), openLockInfo.getType(), openLockInfo.getKeyid(), openLockInfo.getPassword());
    }

    public void onCloudDelete(CloudDeleteEvent cloudDeleteEvent){
        OpenLockInfo openLockInfo = cloudDeleteEvent.getOpenLockInfo();
        cloudDel(openLockInfo.getLockId());
    }
}
