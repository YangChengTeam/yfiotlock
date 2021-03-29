package com.yc.yfiotlock.controller.fragments.user;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.model.bean.lock.ReceiveDeviceInfo;
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
            ReceiveDeviceInfo receiveDeviceInfo = mAdapter.getData().get(position);
            if (view.getId() == R.id.stv_agree) {
                receiveDeviceInfo.state = 1;
                adapter.notifyItemChanged(position, "0");
            }
        });
    }

    private void loadData() {
        List<ReceiveDeviceInfo> receiveDeviceInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ReceiveDeviceInfo receiveDeviceInfo = new ReceiveDeviceInfo();
            receiveDeviceInfo.desp = "来自2307602779";
            receiveDeviceInfo.name = "YF_IOTLOCK";
            receiveDeviceInfo.state = i % 2 == 0 ? 1 : 0;
            receiveDeviceInfos.add(receiveDeviceInfo);
        }
      //  mAdapter.addData(receiveDeviceInfos);
        empty();
    }


    private class DeviceReceiveAdapter extends BaseExtendAdapter<ReceiveDeviceInfo> implements LoadMoreModule {
        public DeviceReceiveAdapter(@Nullable List<ReceiveDeviceInfo> data) {
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

        private void setTextState(BaseViewHolder holder, ReceiveDeviceInfo shareDeviceInfo) {
            if (shareDeviceInfo.state == 1) {
                holder.setVisible(R.id.tv_agreed, true);
                holder.setVisible(R.id.stv_agree, false);
            } else {
                holder.setVisible(R.id.tv_agreed, false);
                holder.setVisible(R.id.stv_agree, true);
            }
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ReceiveDeviceInfo shareDeviceInfo) {
            holder.setText(R.id.tv_device_name, shareDeviceInfo.name);
            holder.setText(R.id.tv_desp, shareDeviceInfo.desp);
            setTextState(holder, shareDeviceInfo);
            setClick(holder.getView(R.id.stv_agree), () -> {
                if (getOnItemChildClickListener() != null) {
                    getOnItemChildClickListener().onItemChildClick(this,
                            holder.getView(R.id.stv_agree), holder.getLayoutPosition());
                }
            });
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
