package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.Dialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.clj.fastble.data.BleDevice;
import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
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
import rx.Subscriber;

public abstract class BaseDetailOpenLockActivity extends BaseBackActivity {
    @BindView(R.id.rv_open_lock)
    RecyclerView openLockRecyclerView;
    @BindView(R.id.stv_del)
    SuperTextView delTv;

    protected OpenLockAdapter openLockAdapter;
    protected LockEngine lockEngine;
    protected OpenLockInfo openLockInfo;
    protected LockBLESend lockBleSend;
    protected int type = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? 2 : 1;

    protected byte mcmd;
    protected byte scmd;

    protected String title;
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
                    cloudDel();
                }
            });
            generalDialog.show();
        });

        setRv();

    }

    protected abstract void bleDel();
    protected abstract void cloudDelSucc();


    protected void cloudDel() {
        mLoadingDialog.show("删除中...");
        lockEngine.delOpenLockWay(openLockInfo.getId() + "").subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> stringResultInfo) {
                if (stringResultInfo.getCode() == 1) {
                    finish();
                    cloudDelSucc();
                    EventBus.getDefault().post(new OpenLockRefreshEvent());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == mcmd && bleData.getScmd() == scmd) {
            if (bleData.getStatus() == (byte) 0x00) {
                cloudDel();
            } else if (bleData.getStatus() == (byte) 0x01) {

            } else if (bleData.getStatus() == (byte) 0x10) {

            } else if (bleData.getStatus() == (byte) 0x11) {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockInfo openLockInfo) {
        List<OpenLockInfo> openLockTypeInfos = new ArrayList<>();
        openLockTypeInfos.add(openLockInfo);
        openLockAdapter.setNewInstance(openLockTypeInfos);
        EventBus.getDefault().post(new OpenLockRefreshEvent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        lockBleSend.clear();
    }

    private void setRv() {
        List<OpenLockInfo> openLockTypeInfos = new ArrayList<>();
        openLockTypeInfos.add(openLockInfo);
        openLockAdapter = new OpenLockAdapter(openLockTypeInfos);
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
            if (holder.getAdapterPosition() == getData().size() - 1) {
                holder.setVisible(R.id.view_line, false);
            } else {
                holder.setVisible(R.id.view_line, true);
            }
        }
    }
}
