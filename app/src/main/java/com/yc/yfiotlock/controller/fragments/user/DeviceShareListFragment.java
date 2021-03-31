package com.yc.yfiotlock.controller.fragments.user;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockShareManageActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ShareDeviceWrapper;
import com.yc.yfiotlock.model.engin.ShareDeviceEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDeviceView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import rx.Observer;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/27
 **/
public class DeviceShareListFragment extends BaseFragment {
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    public DeviceShareListFragment() {
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

    DeviceShareAdapter mAdapter;

    private void setRv() {
        mAdapter = new DeviceShareAdapter(null);
        mRvList.setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvList);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            ShareDeviceWrapper wrapper=mAdapter.getData().get(position);
            DeviceInfo deviceInfo= new DeviceInfo();
            deviceInfo.setId(wrapper.getId());
            deviceInfo.setName(wrapper.getName());
            LockShareManageActivity.start(getContext(),deviceInfo);
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            p++;
            loadData();
        });
    }

    @Override
    protected void initVars() {
        super.initVars();
        mEngine = new ShareDeviceEngine(getContext());
    }

    private ShareDeviceEngine mEngine;

    private void loadData() {
        mSrlRefresh.setRefreshing(p == 1);
        mEngine.getAllDevice(p).subscribe(new Observer<ResultInfo<List<ShareDeviceWrapper>>>() {
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
        if (mEngine!=null){
            mEngine.cancelAll();
        }
    }

    private class DeviceShareAdapter extends BaseExtendAdapter<ShareDeviceWrapper> implements LoadMoreModule {
        public DeviceShareAdapter(@Nullable List<ShareDeviceWrapper> data) {
            super(R.layout.item_device_share, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ShareDeviceWrapper shareDeviceInfo) {
            holder.setText(R.id.tv_device_name, shareDeviceInfo.getName());
            holder.setText(R.id.tv_desp, "分享至" + shareDeviceInfo.getReceiveUser().getMobile());
        }
    }

}
