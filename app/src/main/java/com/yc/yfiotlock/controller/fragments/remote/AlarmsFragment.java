package com.yc.yfiotlock.controller.fragments.remote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.WarnInfo;
import com.yc.yfiotlock.model.bean.WarnListInfo;
import com.yc.yfiotlock.model.engin.LogEngine;
import com.yc.yfiotlock.view.adapters.WarnAdapter;

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
    private int lockerId = 3;

    private WarnAdapter warnAdapter;

    private LogEngine logEngine;

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
        warnAdapter = new WarnAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(warnAdapter);
    }


    private void loadData() {
        logEngine.getWarnLog(lockerId, page, pageSize).subscribe(new Observer<ResultInfo<WarnListInfo>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onNext(ResultInfo<WarnListInfo> logListInfoResultInfo) {
                if (logListInfoResultInfo.getData() != null && logListInfoResultInfo.getData().getItems() != null) {
                    List<WarnInfo> items = logListInfoResultInfo.getData().getItems();
                    warnAdapter.setNewInstance(items);
                }
            }
        });
    }
}
