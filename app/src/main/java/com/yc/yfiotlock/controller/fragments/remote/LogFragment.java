package com.yc.yfiotlock.controller.fragments.remote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.LogInfo;
import com.yc.yfiotlock.model.bean.LogListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.view.adapters.LogAdapter;

import java.util.List;

import butterknife.BindView;
import rx.Observer;

public class LogFragment extends BaseFragment {

    private LogAdapter logAdapter;
    @BindView(R.id.rv_log)
    RecyclerView recyclerView;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private LogEngine logEngine;

    private int page = 1;
    private int pageSize = 10;
    private int lockerId = 3;

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
            loadData();
        });
        mSrlRefresh.setRefreshing(true);

        loadData();
    }


    private void initRv() {
        logAdapter = new LogAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(logAdapter);
    }


    private void loadData() {
        logEngine.getOpenLog(lockerId, page, pageSize).subscribe(new Observer<ResultInfo<LogListInfo>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onNext(ResultInfo<LogListInfo> logListInfoResultInfo) {
                if (logListInfoResultInfo.getData() != null && logListInfoResultInfo.getData().getItems() != null) {
                    List<LogInfo> items = logListInfoResultInfo.getData().getItems();
                    logAdapter.setNewInstance(items);
                }
            }
        });
    }
}
