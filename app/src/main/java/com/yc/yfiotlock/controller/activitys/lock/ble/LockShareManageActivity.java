package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
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
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ShareDeviceWrapper;
import com.yc.yfiotlock.model.engin.ShareDeviceEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDeviceView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.Observer;

public class LockShareManageActivity extends BaseBackActivity {


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

    public static void start(Context context, DeviceInfo deviceInfo) {
        Intent intent = new Intent(context, LockShareManageActivity.class);
        intent.putExtra("deviceInfo", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_lock_share_manage;
    }

    private int p = 1;
    DeviceInfo deviceInfo;

    @Override
    protected void initViews() {
        super.initViews();
        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("deviceInfo");
        String title = deviceInfo == null ? "" : deviceInfo.getName() + "共享管理";
        backNavBar.setTitle(title);
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
            startActivity(new Intent(getContext(), LockShareInputActivity.class));
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
                GeneralDialog dialog = new GeneralDialog(getContext());
                dialog.setTitle("温馨提示")
                        .setMsg("确定删除" + mAdapter.getData().get(position).getReceiveUser().getMobile() + "的使用权限?")
                        .setOnPositiveClickListener(dialog1 -> {
                            deleteUsePermission(position);
                        }).show();
            }
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            p++;
            loadData();
        });
        mAdapter.setEmptyView(new NoDeviceView(getContext()));
    }

    private void deleteUsePermission(int position) {
        mLoadingDialog.show("请求中...");
        mEngine.deleteShare(1, mAdapter.getData().get(position).getId()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), "请求失败");
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                ToastCompat.show(getContext(), info.getMsg());
                if (info.getCode() == 1) {
                    mAdapter.getData().remove(position);
                    mAdapter.notifyItemRemoved(position);
                }
            }
        });
    }

    ShareDeviceEngine mEngine;

    @Override
    protected void initVars() {
        super.initVars();
        mEngine = new ShareDeviceEngine(getContext());
    }

    private void loadData() {
        mSrlRefresh.setRefreshing(p == 1);
        mEngine.getShareList(p, deviceInfo.getId()).subscribe(new Observer<ResultInfo<List<ShareDeviceWrapper>>>() {
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
            public void onNext(ResultInfo<List<ShareDeviceWrapper>> listResultInfo) {
                if (listResultInfo.getCode() == 1) {
                    if (listResultInfo.getData() == null || listResultInfo.getData().size() == 0) {
                        empty();
                        return;
                    }
                    success(listResultInfo);
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
    public void fail() {
        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(new NoWifiView(getContext()));
        } else {
            p--;
            mAdapter.getLoadMoreModule().loadMoreFail();
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
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.cancelAll();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(String s) {
        if (s.equals(ShareDeviceEngine.SHARE_DEVICE_SUCCESS)) {
            p = 1;
            loadData();
        }
    }

    private class LockShareAdapter extends BaseExtendAdapter<ShareDeviceWrapper> implements LoadMoreModule {
        public LockShareAdapter(@Nullable List<ShareDeviceWrapper> data) {
            super(R.layout.item_lock_share, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, ShareDeviceWrapper shareLockInfo) {
            Glide.with(getContext())
                    .load(shareLockInfo.getReceiveUser().getFace())
                    .error(R.mipmap.head_default)
                    .placeholder(R.mipmap.head_default)
                    .circleCrop()
                    .into((ImageView) holder.getView(R.id.iv_face));
            holder.setText(R.id.tv_desp, "共享至".concat(shareLockInfo.getReceiveUser().getMobile()));
            String time = new SimpleDateFormat("yyyy/MM/dd aHH:mm", Locale.CHINA)
                    .format(new Date(Long.parseLong(shareLockInfo.getShareTime().concat("000"))));
            holder.setText(R.id.tv_time, time.concat("共享"));

            // '状态 0:等待接受 1:接受 2:共享人删除',

            if (shareLockInfo.getShareStatus() == 1) {
                holder.setText(R.id.tv_state, "已接受");
                holder.setTextColor(R.id.tv_state, 0xff09B857);
            } else if (shareLockInfo.getShareStatus() == 0) {
                holder.setText(R.id.tv_state, "等待接受");
                holder.setTextColor(R.id.tv_state, 0xff3395FD);
            } else {
                holder.setText(R.id.tv_state, "共享人删除");
                holder.setTextColor(R.id.tv_state, 0xff666666);
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