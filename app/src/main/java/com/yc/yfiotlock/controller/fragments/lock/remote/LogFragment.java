package com.yc.yfiotlock.controller.fragments.lock.remote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.adapters.LogAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Observer;

public class LogFragment extends BaseFragment {

    @BindView(R.id.rv_log)
    RecyclerView recyclerView;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    protected LogEngine logEngine;
    protected LogAdapter logAdapter;
    protected LockLogDao lockLogDao;
    protected DeviceInfo lockInfo;

    protected int type = 1;
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
    protected void initViews() {
        initRv();

        logEngine = new LogEngine(getActivity());

        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            page = 1;
            localLoadData();
        });
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

    private void localLoadData() {
        mSrlRefresh.setRefreshing(true);
        lockLogDao.loadLogInfos(lockInfo.getId(), type, page, pageSize).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<LogInfo>>() {
            @Override
            public void accept(List<LogInfo> openLockInfos) throws Exception {
                success(openLockInfos);
                if (CommonUtil.isNetworkAvailable(getContext()) && openLockInfos.size() == 0) {
                    cloudLoadData();
                }
            }
        });
    }

    protected void cloudLoadData() {
        logEngine.getLocalOpenLog(lockInfo.getId() + "", page, pageSize).subscribe(new Observer<ResultInfo<LogListInfo>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
                fail();
            }

            @Override
            public void onNext(ResultInfo<LogListInfo> info) {
                if (info != null && info.getCode() == 1) {
                    if (info.getData() == null || info.getData().getItems() == null || info.getData().getItems().size() == 0) {
                        empty();
                        return;
                    }
                    sync2Local(info.getData().getItems());
                } else {
                    fail();
                }
            }
        });
    }

    public void sync2Local(List<LogInfo> data) {
        if (data.size() == pageSize) {
            lockLogDao.insertLogInfos(data).subscribeOn(Schedulers.io()).subscribe();
            page++;
            cloudLoadData();
        }
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
    public void fail() {
        super.fail();
        if (logAdapter.getData().size() == 0) {
            logAdapter.setNewInstance(null);
            logAdapter.setEmptyView(new NoWifiView(getActivity()));
        } else {
            page--;
            logAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    @Override
    public void empty() {
        super.empty();
        if (logAdapter.getData().size() == 0) {
            logAdapter.setNewInstance(null);
            logAdapter.setEmptyView(new NoDataView(getActivity()));
        } else {
            page--;
            logAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }
}
