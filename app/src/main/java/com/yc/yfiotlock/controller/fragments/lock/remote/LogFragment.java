package com.yc.yfiotlock.controller.fragments.lock.remote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.view.adapters.LogAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import java.util.List;

import butterknife.BindView;
import rx.Observer;

public class LogFragment extends BaseFragment {

    @BindView(R.id.rv_log)
    RecyclerView recyclerView;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private LogEngine logEngine;
    private LogAdapter logAdapter;


    private int page = 1;
    private int pageSize = 20;
    private DeviceInfo deviceInfo;

    public LogFragment() {
    }

    public LogFragment(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
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
            loadData();
        });
        mSrlRefresh.setRefreshing(true);

        loadData();
    }


    private void initRv() {
        logAdapter = new LogAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(logAdapter);

        logAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            page++;
            loadData();
        });
    }


    private void loadData() {
        if (deviceInfo == null) {
            return;
        }
        logEngine.getOpenLog(deviceInfo.getId()+"", page, pageSize).subscribe(new Observer<ResultInfo<LogListInfo>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
                loadDateFail();
            }

            @Override
            public void onNext(ResultInfo<LogListInfo> logListInfoResultInfo) {
                if (logListInfoResultInfo.getData() == null || logListInfoResultInfo.getData().getItems() == null || logListInfoResultInfo.getData().getItems().size() == 0) {
                    loadDateEmpty();
                    return;
                }

                List<LogInfo> items = logListInfoResultInfo.getData().getItems();
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
        });
    }

    private void loadDateFail() {
        if (page == 1) {
            logAdapter.setNewInstance(null);
            logAdapter.setEmptyView(new NoWifiView(getActivity()));
        } else {
            page--;
            logAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    private void loadDateEmpty() {
        if (page == 1) {
            logAdapter.setNewInstance(null);
            logAdapter.setEmptyView(new NoDataView(getActivity()));
        } else {
            page--;
            logAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }
}
