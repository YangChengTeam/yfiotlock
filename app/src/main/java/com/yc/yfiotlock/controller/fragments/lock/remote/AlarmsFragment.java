package com.yc.yfiotlock.controller.fragments.lock.remote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.model.bean.eventbus.LockLogSyncDataEvent;
import com.yc.yfiotlock.model.bean.eventbus.LockLogSyncEndEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogListInfo;
import com.yc.yfiotlock.model.bean.lock.remote.WarnInfo;
import com.yc.yfiotlock.model.bean.lock.remote.WarnListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.view.adapters.WarnAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.functions.Action1;

public class AlarmsFragment extends LogFragment {
    public AlarmsFragment() {
    }

    public AlarmsFragment(LockLogDao lockLogDao, DeviceInfo lockInfo) {
        this.lockInfo = lockInfo;
        this.lockLogDao = lockLogDao;
        type = 2;
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
