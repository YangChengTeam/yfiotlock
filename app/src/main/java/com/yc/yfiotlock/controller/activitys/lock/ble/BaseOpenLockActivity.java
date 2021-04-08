package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
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
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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

    protected int groupType = 1;
    protected int type = 1;
    protected OpenLockAdapter openLockAdapter;
    protected LockEngine lockEngine;
    protected OpenLockDao openLockDao;
    protected DeviceInfo lockInfo;
    protected List<OpenLockInfo> allOpenLockInfos;
    protected boolean isAdmin = true;
    protected String title;


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
        groupType = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? 2 : 1;
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
            loadData();
        });

        loadData();
    }

    private void loadData() {
        if (lockInfo.isShare()) {
            cloudLoadData();
        } else {
            localLoadData();
        }
    }

    private void setRv() {
        openLockAdapter = new OpenLockAdapter(null);
        openLockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openLockRecyclerView.setAdapter(openLockAdapter);
    }

    private void localLoadData() {
        localLoadData(false);
    }

    @SuppressLint("CheckResult")
    private void localLoadData(boolean isOnlyLocal) {
        nodataView.setVisibility(View.GONE);
        noWifiView.setVisibility(View.GONE);
        mSrlRefresh.setRefreshing(true);
        openLockDao.loadOpenLockInfos(lockInfo.getId(), type, LockBLEManager.GROUP_TYPE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                allOpenLockInfos = openLockInfos;
                List<OpenLockInfo> lastOpenLocks = new ArrayList<>();
                for (OpenLockInfo openLockInfo : openLockInfos) {
                    if (!openLockInfo.isDelete()) {
                        lastOpenLocks.add(openLockInfo);
                    }
                }

                openLockAdapter.setNewInstance(lastOpenLocks);
                if (isOnlyLocal) {
                    mSrlRefresh.setRefreshing(false);
                    empty();
                    return;
                }

                if (CommonUtil.isNetworkAvailable(getContext())) {
                    cloudLoadData();
                } else {
                    mSrlRefresh.setRefreshing(false);
                    empty();
                }
            }
        });
    }

    private void cloudLoadData() {
        if (lockInfo.isShare()) {
            nodataView.setVisibility(View.GONE);
            noWifiView.setVisibility(View.GONE);
            mSrlRefresh.setRefreshing(true);
        }
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
                if (info != null && info.getCode() == 1) {
                    if (info.getData() == null || info.getData().size() == 0) {
                        empty();
                        setCountInfo();
                        // 分享设备同步网络数据到本地
                        if (lockInfo.isShare()) {
                            openLockDao.deleteOpenLockInfos(lockInfo.getId(), type, LockBLEManager.GROUP_TYPE).subscribeOn(Schedulers.io()).subscribe();
                        }
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

    private void processData(List<OpenLockInfo> copenlockInfos) {
        for (OpenLockInfo openLockInfo : copenlockInfos) {
            openLockInfo.setAdd(true);
            openLockInfo.setType(type);
            openLockInfo.setGroupType(LockBLEManager.GROUP_TYPE);
            openLockInfo.setMasterLockId(lockInfo.getId());
            if (openLockInfo.getLockId() == 0) {
                openLockInfo.setLockId(lockInfo.getId());
            }
        }
    }

    public abstract void setCountInfo();

    @SuppressLint("CheckResult")
    @Override
    public void success(Object data) {
        List<OpenLockInfo> copenlockInfos = (List<OpenLockInfo>) data;

        // 分享设备同步网络数据到本地
        if (lockInfo.isShare()) {
            openLockAdapter.setNewInstance(copenlockInfos);
            processData(copenlockInfos);
            openLockDao.deleteOpenLockInfos(lockInfo.getId(), type, LockBLEManager.GROUP_TYPE).subscribeOn(Schedulers.io()).subscribe(new Action() {
                @Override
                public void run() throws Exception {
                    openLockDao.insertOpenLockInfos(copenlockInfos).subscribeOn(Schedulers.io()).subscribe();
                }
            });
            setCountInfo();
            return;
        }

        // 主设备 diff数据同步到本地
        List<OpenLockInfo> lastOpenLockInfos = new ArrayList<>();
        for (OpenLockInfo copenLockInfo : copenlockInfos) {
            boolean isExist = false;
            for (OpenLockInfo lopenLockInfo : allOpenLockInfos) {
                if (lopenLockInfo.getKeyid() == copenLockInfo.getKeyid()) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                lastOpenLockInfos.add(copenLockInfo);
            }
        }
        if (lastOpenLockInfos.size() > 0) {
            processData(lastOpenLockInfos);
            openLockDao.insertOpenLockInfos(lastOpenLockInfos).subscribeOn(Schedulers.io()).subscribe();
            lastOpenLockInfos.addAll(openLockAdapter.getData());
            lastOpenLockInfos.sort(new Comparator<OpenLockInfo>() {
                @Override
                public int compare(OpenLockInfo o1, OpenLockInfo o2) {
                    return o2.getId() - o1.getId();
                }
            });
            openLockAdapter.setNewInstance(lastOpenLockInfos);
        }
        empty();
        setCountInfo();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        //添加、更新、删除 直接显示本地数据
        localLoadData(true);
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
