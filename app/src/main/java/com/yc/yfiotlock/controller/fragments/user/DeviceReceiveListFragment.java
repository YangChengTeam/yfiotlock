package com.yc.yfiotlock.controller.fragments.user;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ShareDeviceWrapper;
import com.yc.yfiotlock.model.engin.ShareDeviceEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDeviceView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import rx.Observer;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/29
 **/
public class DeviceReceiveListFragment extends BaseFragment {
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    public DeviceReceiveListFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment_device_share_list;
    }

    private int p = 1;

    @Override
    protected void initViews() {
        setRv();
        mSrlRefresh.setColorSchemeColors(0xff3395FD);
        mSrlRefresh.setOnRefreshListener(() -> {
            p = 1;
            loadData();
        });
        loadData();
    }

    @Override
    protected void initVars() {
        super.initVars();
        mEngine = new ShareDeviceEngine(getContext());
    }

    ShareDeviceEngine mEngine;
    DeviceReceiveAdapter mAdapter;

    private void setRv() {
        mAdapter = new DeviceReceiveAdapter(null);
        mRvList.setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvList);

        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            p++;
            loadData();
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.stv_agree) {
                agreeShare(position);
            }
        });
    }

    private void loadData() {
        mSrlRefresh.setRefreshing(p == 1);
        mEngine.getReceiveList(p).subscribe(new Observer<ResultInfo<List<ShareDeviceWrapper>>>() {
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
            public void onNext(ResultInfo<List<ShareDeviceWrapper>> info) {
                if (info.getCode() == 1) {
                    if (info.getData() == null || info.getData().size() == 0) {
                        empty();
                        return;
                    }
                    success(info);
                } else {
                    fail();
                }
            }
        });
    }

    private void agreeShare(int position) {
        ShareDeviceWrapper receiveDeviceInfo = mAdapter.getData().get(position);
        mLoadingDialog.show("添加中...");
        String msg = "添加失败";

        mEngine.receiveShare(receiveDeviceInfo.getId() + "").subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), e.getMessage());
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info.getCode() == 1) {
                    mLoadingDialog.dismiss();
                    receiveDeviceInfo.setShareStatus(1);
                    UserInfoCache.incDeviceNumber();
                    mAdapter.notifyItemChanged(position, "0");
                    EventBus.getDefault().post(new IndexRefreshEvent());
                } else {
                    String tmsg = msg;
                    tmsg = info != null && info.getMsg() != null ? info.getMsg() : tmsg;
                    ToastCompat.show(getContext(), tmsg);
                }
            }
        });
    }

    @Override
    public void empty() {
        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(new NoDeviceView(getContext()));
        } else {
            mAdapter.getLoadMoreModule().loadMoreEnd();
        }
    }

    @Override
    public void success(Object data) {
        List<ShareDeviceWrapper> list = ((ResultInfo<List<ShareDeviceWrapper>>) data).getData();
        if (p == 1) {
            mAdapter.setNewInstance(list);
        } else {
            mAdapter.addData(list);
        }
        if (list.size() < 10) {
            mAdapter.getLoadMoreModule().loadMoreEnd();
        } else {
            mAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    @Override
    public void fail() {
        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(new NoWifiView(getContext()));
        } else {
            p--;
            mAdapter.getLoadMoreModule().loadMoreFail();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.cancelAll();
        }
    }


    private class DeviceReceiveAdapter extends BaseExtendAdapter<ShareDeviceWrapper> implements LoadMoreModule {
        public DeviceReceiveAdapter(@Nullable List<ShareDeviceWrapper> data) {
            super(R.layout.item_device_receive, data);
        }

        @Override
        public void onBindViewHolder(@NotNull BaseViewHolder holder, int position, @NotNull List<Object> payloads) {
            if (payloads.size() > 0) {
                setTextState(holder, getData().get(position));
            } else {
                super.onBindViewHolder(holder, position);
            }
        }

        private void setTextState(BaseViewHolder holder, ShareDeviceWrapper shareDeviceInfo) {
            if (shareDeviceInfo.getShareStatus() == 1) {
                holder.setVisible(R.id.tv_agreed, true);
                holder.setVisible(R.id.stv_agree, false);
            } else {
                holder.setVisible(R.id.tv_agreed, false);
                holder.setVisible(R.id.stv_agree, true);
            }
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ShareDeviceWrapper shareDeviceInfo) {
            holder.setText(R.id.tv_device_name, shareDeviceInfo.getLocker().getName());
            holder.setText(R.id.tv_desp, "来自" + shareDeviceInfo.getShareUser().getMobile());
            setTextState(holder, shareDeviceInfo);
            setClick(holder.getView(R.id.stv_agree), () -> {
                if (getOnItemChildClickListener() != null) {
                    getOnItemChildClickListener().onItemChildClick(this,
                            holder.getView(R.id.stv_agree), holder.getLayoutPosition());
                }
            });
        }
    }

}
