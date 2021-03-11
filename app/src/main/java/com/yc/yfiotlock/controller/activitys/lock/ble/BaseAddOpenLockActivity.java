package com.yc.yfiotlock.controller.activitys.lock.ble;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.engin.LockEngine;

import org.greenrobot.eventbus.EventBus;

import rx.Subscriber;

public abstract class BaseAddOpenLockActivity extends BaseBackActivity {
    protected LockEngine lockEngine;
    protected DeviceInfo lockInfo;

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
    }

    protected abstract void cloudAddSucc();

    protected void cloudAdd(String name, int type, String keyid, String password) {
        mLoadingDialog.show("添加中...");
        lockEngine.addOpenLockWay(lockInfo.getId(), name, keyid, type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> stringResultInfo) {
                if (stringResultInfo.getCode() == 1) {
                    finish();
                    cloudAddSucc();
                    EventBus.getDefault().post(new OpenLockRefreshEvent());
                }
            }
        });
    }
}
