package com.yc.yfiotlock.controller.fragments.lock.remote;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.lock.remote.LockLogActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.model.bean.eventbus.LockLogSyncDataEvent;
import com.yc.yfiotlock.model.bean.eventbus.LockLogSyncEndEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.adapters.LogAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Observer;
import rx.functions.Action1;

public class LogFragment extends BaseFragment {

    @BindView(R.id.rv_log)
    RecyclerView recyclerView;

    @BindView(R.id.view_no_data)
    NoDataView nodataView;

    protected LogEngine logEngine;
    protected LogAdapter logAdapter;
    protected LockLogDao lockLogDao;
    protected DeviceInfo lockInfo;

    protected int logtype = LockLogActivity.LOG_TYPE;
    protected int page = 1;
    protected int pageSize = 200;

    public LogFragment() {
    }

    public LogFragment(LockLogDao lockLogDao, DeviceInfo lockInfo) {
        this.lockInfo = lockInfo;
        this.lockLogDao = lockLogDao;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_log;
    }

    @Override
    protected void initVars() {
        super.initVars();
        logEngine = new LogEngine(getActivity());
    }

    @Override
    protected void initViews() {
        initRv();
        cloudLoadData();
        localLoadData();
    }


    private void initRv() {
        logAdapter = new LogAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(logAdapter);
        logAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            page++;
            localLoadData();
        });
    }

    @SuppressLint("CheckResult")
    protected void localLoadData() {
        lockLogDao.loadLogInfos(lockInfo.getId(), logtype, page, pageSize).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<LogInfo>>() {
            @Override
            public void accept(List<LogInfo> openLockInfos) throws Exception {
                if (openLockInfos.size() != 0) {
                    nodataView.setVisibility(View.GONE);
                    success(openLockInfos);
                } else {
                    nodataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void cloudLoadData() {
        logEngine.getLocalOpenLog(lockInfo.getId() + "", 1, pageSize).subscribe(new Action1<ResultInfo<LogListInfo>>() {
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

    @SuppressLint("CheckResult")
    public void sync2Local(List<LogInfo> logInfos) {
        for (LogInfo logInfo : logInfos) {
            logInfo.setAddtime(System.currentTimeMillis());
            logInfo.setLogType(logtype);
        }
        lockLogDao.insertLogInfos(logInfos).subscribeOn(Schedulers.io()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                localLoadData();
            }
        });
    }

    @Override
    public void success(Object data) {
        super.success(data);
        List<LogInfo> items = (List<LogInfo>) data;
        if (page == 1) {
            logAdapter.setNewInstance(items);
        } else {
            logAdapter.addData(items);
        }

        if (items.size() < pageSize) {
            logAdapter.getLoadMoreModule().loadMoreEnd();
        } else {
            logAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    @Override
    public void empty() {
        if (CommonUtil.isActivityDestory(getActivity())) return;
        super.empty();
        if (logAdapter.getData().size() == 0) {
            logAdapter.setEmptyView(new NoDataView(getActivity()));
        } else {
            page--;
            logAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSync(LockLogSyncDataEvent object) {
        page = 1;
        localLoadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSync(LockLogSyncEndEvent object) {
        page = 1;
        localLoadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logEngine.cancelAll();
    }
}
