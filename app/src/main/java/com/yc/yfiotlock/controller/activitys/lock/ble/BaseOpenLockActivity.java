package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.BleUtil;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

public abstract class BaseOpenLockActivity extends BaseBackActivity {
    @BindView(R.id.rv_open_lock)
    RecyclerView openLockRecyclerView;
    @BindView(R.id.view_no_data)
    NoDataView nodataView;
    @BindView(R.id.view_no_wifi)
    NoWifiView noWifiView;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    @BindView(R.id.stv_add)
    protected SuperTextView addBtn;

    protected int groupType = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? 2 : 1;
    protected int type = 1;
    protected OpenLockAdapter openLockAdapter;
    protected LockEngine lockEngine;
    protected OpenLockDao openLockDao;
    protected DeviceInfo lockInfo;

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_base_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
        openLockDao = App.getApp().getDb().openLockDao();
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
    }

    @Override
    protected void initViews() {
        super.initViews();
        type = BleUtil.getType(title);

        setNavTitle(title);
        addBtn.setText(addBtn.getText() + title);

        setRv();

        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            localLoadData();
        });

        localLoadData();
    }

    private void setRv() {
        openLockAdapter = new OpenLockAdapter(null);
        openLockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openLockRecyclerView.setAdapter(openLockAdapter);
    }

    private void localLoadData() {
        mSrlRefresh.setRefreshing(true);
        openLockDao.loadOpenLockInfos(lockInfo.getId(), type, LockBLEManager.GROUP_TYPE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                openLockAdapter.setNewInstance(openLockInfos);
                cloudLoadData();
            }
        });

    }

    private void cloudLoadData() {
        nodataView.setVisibility(View.GONE);
        noWifiView.setVisibility(View.GONE);
        lockEngine.getOpenLockWayList(lockInfo.getId() + "", type + "", groupType + "").subscribe(new Subscriber<ResultInfo<List<OpenLockInfo>>>() {
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
            public void onNext(ResultInfo<List<OpenLockInfo>> info) {
                if (info.getCode() == 1 && info.getData() != null) {
                    if (info.getData() == null || info.getData().size() == 0) {
                        empty();
                    } else {
                        success(info.getData());
                    }
                } else {
                    fail();
                }
            }
        });
    }

    @Override
    public void empty() {
        if (openLockAdapter.getData().size() == 0) {
            nodataView.setVisibility(View.VISIBLE);
            nodataView.setMessage("暂无" + title + "数据");
        }
    }

    @Override
    public void fail() {
        if (openLockAdapter.getData().size() == 0) {
            noWifiView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void success(Object data) {
        List<OpenLockInfo> lastSyncLockInfos = new ArrayList<>();
        List<OpenLockInfo> lockInfos = (List<OpenLockInfo>) data;
        if (lockInfos.size() > 0) {
            lastSyncLockInfos.addAll(lockInfos);
            for (OpenLockInfo lopenLockInfo : openLockAdapter.getData()) {
                boolean isExist = false;
                for (OpenLockInfo copenLockInfo : lockInfos) {
                    if (copenLockInfo.getKeyid() == lopenLockInfo.getKeyid()) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    lastSyncLockInfos.add(lopenLockInfo);
                }
            }
            openLockAdapter.setNewInstance(lastSyncLockInfos);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        cloudLoadData();
    }

    public static class OpenLockAdapter extends BaseExtendAdapter<OpenLockInfo> {
        public OpenLockAdapter(@Nullable List<OpenLockInfo> data) {
            super(R.layout.lock_ble_item_base_open_lock, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, OpenLockInfo openLockInfo) {
            holder.setText(R.id.tv_name, openLockInfo.getName());
            String text = "我".equals(openLockInfo.getAddUserMobile()) ? "" : "用户";
            holder.setText(R.id.tv_from, text + openLockInfo.getAddUserMobile() + "添加");
            if (holder.getAdapterPosition() == getData().size() - 1) {
                holder.setVisible(R.id.view_line, false);
            } else {
                holder.setVisible(R.id.view_line, true);
            }
        }
    }
}
