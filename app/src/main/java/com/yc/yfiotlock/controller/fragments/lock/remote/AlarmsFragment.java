package com.yc.yfiotlock.controller.fragments.lock.remote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.WarnInfo;
import com.yc.yfiotlock.model.bean.lock.remote.WarnListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.view.adapters.WarnAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import java.util.List;

import butterknife.BindView;
import rx.Observer;

public class AlarmsFragment extends BaseFragment {


    @BindView(R.id.rv_log)
    RecyclerView recyclerView;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private int page = 1;
    private int pageSize = 10;
    private final DeviceInfo deviceInfo;


    private WarnAdapter warnAdapter;
    private LogEngine logEngine;

    public AlarmsFragment(DeviceInfo deviceInfo) {
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
        warnAdapter = new WarnAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(warnAdapter);

        warnAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            page++;
            loadData();
        });
    }


    private void loadData() {
        if (deviceInfo == null) {
            return;
        }
        logEngine.getWarnLog(deviceInfo.getId(), page, pageSize).subscribe(new Observer<ResultInfo<WarnListInfo>>() {
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
            public void onNext(ResultInfo<WarnListInfo> logListInfoResultInfo) {
                if (logListInfoResultInfo.getData() == null || logListInfoResultInfo.getData().getItems() == null || logListInfoResultInfo.getData().getItems().size() == 0) {
                    loadDateEmpty();
                    return;
                }

                List<WarnInfo> items = logListInfoResultInfo.getData().getItems();
                if (page == 1) {
                    warnAdapter.setNewInstance(items);
                } else {
                    warnAdapter.addData(items);
                }

                if (items.size() < pageSize) {
                    warnAdapter.getLoadMoreModule().loadMoreEnd();
                } else {
                    warnAdapter.getLoadMoreModule().loadMoreComplete();
                }
            }
        });
    }

    private void loadDateFail() {
        if (page == 1) {
            warnAdapter.setNewInstance(null);
            warnAdapter.setEmptyView(new NoWifiView(getContext()));
        } else {
            page--;
            warnAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    private void loadDateEmpty() {
        if (page == 1) {
            warnAdapter.setNewInstance(null);
            warnAdapter.setEmptyView(new NoDataView(getContext()));
        } else {
            page--;
            warnAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }
}
