package com.yc.yfiotlock.controller.fragments.lock.remote;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogListInfo;

import rx.functions.Action1;

public class AlarmsFragment extends LogFragment {
    public AlarmsFragment() {
    }

    public AlarmsFragment(LockLogDao lockLogDao, DeviceInfo lockInfo) {
        this.lockInfo = lockInfo;
        this.lockLogDao = lockLogDao;
        logtype = 2;
    }

    @Override
    protected void cloudLoadData() {
        logEngine.getLocalWarnLog(lockInfo.getId() + "", 1, pageSize).subscribe(new Action1<ResultInfo<LogListInfo>>() {
            @Override
            public void call(ResultInfo<LogListInfo> info) {
                if (info != null && info.getCode() == 1) {
                    if (info.getData() == null || info.getData().getItems() == null || info.getData().getItems().size() == 0) {
                        return;
                    }
                    sync2Local(info.getData().getItems());
                }
            }
        });
    }
}
