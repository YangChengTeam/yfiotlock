package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.ShareLockInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDeviceView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockShareActivity extends BaseBackActivity {


    @BindView(R.id.view_line)
    View mViewLine;
    @BindView(R.id.stv_add)
    SuperTextView mStvAdd;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_lock_share;
    }

    private int p = 1;

    @Override
    protected void initViews() {
        super.initViews();
        mSrlRefresh.setColorSchemeColors(0xff3395fd);
        mSrlRefresh.setOnRefreshListener(() -> {
            p = 1;
            loadData();
        });
        setRvList();
        loadData();
    }

    @Override
    protected void bindClick() {
        setClick(mStvAdd, () -> {
           startActivity(new Intent(getContext(),LockShareInputActivity.class));
        });
    }

    LockShareAdapter mAdapter;

    private void setRvList() {
        mAdapter = new LockShareAdapter(null);
        mRvList.setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvList);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.stv_del) {
                Log.i("aaaa", "setRvList: " + position);
                mAdapter.getData().remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            p++;
            loadData();
        });
        mAdapter.setEmptyView(new NoDeviceView(getContext()));
    }

    private void loadData() {
        mSrlRefresh.setRefreshing(false);
        List<ShareLockInfo> lockInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ShareLockInfo shareLockInfo = new ShareLockInfo();
            shareLockInfo.state = i % 2 == 0 ? 1 : 0;
            shareLockInfo.desp = "共享至71648783";
            shareLockInfo.time = "2021/3/25  上午11:31共享";
            shareLockInfo.face = "https://pics2.baidu.com/feed/962bd40735fae6cda8802d4416624b2c42a70f3c.jpeg?token=cbea20cf09e35451af233a1e46b9fc02";
            lockInfos.add(shareLockInfo);
        }
        mAdapter.addData(lockInfos);
        if (p == 1) {
            mAdapter.getLoadMoreModule().loadMoreEnd();
        }
    }


    private class LockShareAdapter extends BaseExtendAdapter<ShareLockInfo> implements LoadMoreModule {
        public LockShareAdapter(@Nullable List<ShareLockInfo> data) {
            super(R.layout.item_lock_share, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ShareLockInfo shareLockInfo) {
            Glide.with(getContext())
                    .load(shareLockInfo.face)
                    .error(R.mipmap.head_default)
                    .placeholder(R.mipmap.head_default)
                    .circleCrop()
                    .into((ImageView) holder.getView(R.id.iv_face));
            holder.setText(R.id.tv_desp, shareLockInfo.desp);
            holder.setText(R.id.tv_time, shareLockInfo.time);
            if (shareLockInfo.state == 1) {
                holder.setText(R.id.tv_state, "已接受");
                holder.setTextColor(R.id.tv_state, 0xff09B857);
            } else {
                holder.setText(R.id.tv_state, "等待接受");
                holder.setTextColor(R.id.tv_state, 0xff3395FD);
            }

            setClick(holder.getView(R.id.stv_del), () -> {
                if (getOnItemChildClickListener() != null) {
                    getOnItemChildClickListener().onItemChildClick(this,
                            holder.getView(R.id.stv_del), holder.getLayoutPosition());
                }
            });

        }

    }


}