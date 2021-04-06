package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.Dialog;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeleteEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

public abstract class BaseDetailOpenLockActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {
    @BindView(R.id.rv_open_lock)
    RecyclerView openLockRecyclerView;
    @BindView(R.id.stv_del)
    SuperTextView delTv;

    protected OpenLockDao openLockDao;
    protected OpenLockAdapter openLockAdapter;
    protected LockEngine lockEngine;
    protected OpenLockInfo openLockInfo;
    protected LockBLESend lockBleSend;
    protected int type = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? LockBLEManager.ALARM_TYPE : LockBLEManager.NORMAL_TYPE;

    protected byte mcmd;
    protected byte scmd;

    protected String title;
    @BindView(R.id.fl_bottom)
    FrameLayout mFlBottom;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_base_detail_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        openLockDao = App.getApp().getDb().openLockDao();
        DeviceInfo deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        if (deviceInfo != null && deviceInfo.isShare() == 1) {
            mFlBottom.setVisibility(View.GONE);
        }
        lockEngine = new LockEngine(this);
        openLockInfo = (OpenLockInfo) getIntent().getSerializableExtra("openlockinfo");
        BleDevice bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBleSend = new LockBLESend(this, bleDevice);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setNavTitle(title + "详情");

        delTv.setText(delTv.getText() + title);
        RxView.clicks(delTv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            GeneralDialog generalDialog = new GeneralDialog(BaseDetailOpenLockActivity.this);
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("是否删除" + openLockInfo.getName());
            generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    mLoadingDialog.show("删除中...");
                    bleDel();
                }
            });
            generalDialog.show();
        });

        setRv();

    }

    protected abstract void bleDel();

    protected abstract void localDelSucc();

    protected void localDel() {
        DeviceInfo lockInfo = LockIndexActivity.getInstance().getLockInfo();
        openLockDao.deleteOpenLockInfo(lockInfo.getId(), openLockInfo.getKeyid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                retryCount = 3;
                localDelSucc();
                if (CommonUtil.isNetworkAvailable(getContext())) {
                    EventBus.getDefault().post(new CloudDeleteEvent(openLockInfo));
                }
                EventBus.getDefault().post(new OpenLockRefreshEvent());
                finish();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (retryCount-- > 3) {
                    localDel();
                } else {
                    retryCount = 3;
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockInfo openLockInfo) {
        List<OpenLockInfo> openLockInfos = new ArrayList<>();
        openLockInfos.add(openLockInfo);
        openLockAdapter.setNewInstance(openLockInfos);
        EventBus.getDefault().post(new OpenLockRefreshEvent());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(this);
            lockBleSend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
        }
    }

    private void setRv() {
        List<OpenLockInfo> openLockInfos = new ArrayList<>();
        openLockInfos.add(openLockInfo);
        openLockAdapter = new OpenLockAdapter(openLockInfos);
        openLockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openLockRecyclerView.setAdapter(openLockAdapter);
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

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            localDel();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            mLoadingDialog.dismiss();
            ToastCompat.show(getContext(), "删除失败");
        }
    }
}
