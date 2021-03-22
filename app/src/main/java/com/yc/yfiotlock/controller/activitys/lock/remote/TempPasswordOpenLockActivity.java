package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.model.bean.lock.remote.WarnInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.view.adapters.TempPwdAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class TempPasswordOpenLockActivity extends BaseActivity {
    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_temp_pwd)
    RecyclerView recyclerView;
    @BindView(R.id.stv_add)
    SuperTextView stvAdd;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private TempPwdAdapter tempPwdAdapter;
    private LockEngine lockEngine;

    private int page = 1;
    private int pageSize = 10;


    public static void start(Context context, DeviceInfo deviceInfo) {
        Intent intent = new Intent(context, TempPasswordOpenLockActivity.class);
        intent.putExtra("device", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_password_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();

        lockEngine = new LockEngine(this);
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        RxView.clicks(stvAdd).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            Serializable device = getIntent().getSerializableExtra("device");
            if (device instanceof DeviceInfo) {
                CreatPwdActivity.start(TempPasswordOpenLockActivity.this, (DeviceInfo) device);
            }
        });

        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            page = 1;
            loadData();
        });

        mSrlRefresh.setRefreshing(true);
        loadData();
    }

    private void initRv() {
        tempPwdAdapter = new TempPwdAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(tempPwdAdapter);

        tempPwdAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                TempPwdDetailActivity.start(TempPasswordOpenLockActivity.this, tempPwdAdapter.getItem(position));
            }
        });

        tempPwdAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            page++;
            loadData();
        });
    }

    private void loadData() {
        Serializable device = getIntent().getSerializableExtra("device");
        if (!(device instanceof DeviceInfo)) {
            ToastUtil.toast2(TempPasswordOpenLockActivity.this, "未连接设备");
            return;
        }
        lockEngine.temporaryPwdList(((DeviceInfo) device).getId(), page, pageSize).subscribe(new Subscriber<ResultInfo<List<PassWordInfo>>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
                loadDataFail();
            }

            @Override
            public void onNext(ResultInfo<List<PassWordInfo>> listResultInfo) {
                if (listResultInfo != null && listResultInfo.getCode() == 1) {
                    if (listResultInfo.getData() != null && listResultInfo.getData().size() != 0) {
                        List<PassWordInfo> items = listResultInfo.getData();
                        if (page == 1) {
                            tempPwdAdapter.setNewInstance(items);
                        } else {
                            tempPwdAdapter.addData(items);
                        }

                        if (items.size() < pageSize) {
                            tempPwdAdapter.getLoadMoreModule().loadMoreEnd();
                        } else {
                            tempPwdAdapter.getLoadMoreModule().loadMoreComplete();
                        }
                    } else {
                        loadDataEmpty();
                    }
                } else {
                    loadDataFail();
                }
            }

            private void loadDataEmpty() {
                if (page == 1) {
                    tempPwdAdapter.setNewInstance(null);
                    tempPwdAdapter.setEmptyView(new NoDataView(getContext()));
                } else {
                    page--;
                    tempPwdAdapter.getLoadMoreModule().loadMoreComplete();
                }
            }

            private void loadDataFail() {
                if (page == 1) {
                    tempPwdAdapter.setNewInstance(null);
                    tempPwdAdapter.setEmptyView(new NoWifiView(getContext()));
                } else {
                    page--;
                    tempPwdAdapter.getLoadMoreModule().loadMoreComplete();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        page = 1;
        loadData();
    }
}
