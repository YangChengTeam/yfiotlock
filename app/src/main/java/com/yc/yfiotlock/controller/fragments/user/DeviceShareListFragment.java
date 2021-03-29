package com.yc.yfiotlock.controller.fragments.user;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.model.bean.lock.ShareDeviceInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDeviceView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {

        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            p++;
            loadData();
        });
    }

    private void loadData() {
        List<ShareDeviceInfo> shareDeviceInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ShareDeviceInfo shareDeviceInfo = new ShareDeviceInfo();
            shareDeviceInfo.desp = "分享至4164565465";
            shareDeviceInfo.name = "YF_IOTLOCK";
            shareDeviceInfos.add(shareDeviceInfo);
        }
        mAdapter.addData(shareDeviceInfos);
    }


    private class DeviceShareAdapter extends BaseExtendAdapter<ShareDeviceInfo> implements LoadMoreModule {
        public DeviceShareAdapter(@Nullable List<ShareDeviceInfo> data) {
            super(R.layout.item_device_share, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ShareDeviceInfo shareDeviceInfo) {
            holder.setText(R.id.tv_device_name, shareDeviceInfo.name);
            holder.setText(R.id.tv_desp, shareDeviceInfo.desp);
        }
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
        super.success(data);
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
}
