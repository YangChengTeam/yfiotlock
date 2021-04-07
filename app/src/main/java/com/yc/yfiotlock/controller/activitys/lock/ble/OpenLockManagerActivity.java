package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockCountRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OpenLockManagerActivity extends BaseBackActivity {

    @BindView(R.id.rv_open_lock)
    RecyclerView openLockRecyclerView;

    protected DeviceInfo lockInfo;
    protected OpenLockAdapter openLockAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_open_lock_manager;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
        LockBLEManager.GROUP_TYPE = LockBLEManager.GROUP_ADMIN;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRv();
        loadData();
    }

    private void setRv() {
        openLockAdapter = new OpenLockAdapter(null);
        openLockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openLockRecyclerView.setAdapter(openLockAdapter);
        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (!LockBLEManager.isConnected(LockIndexActivity.getInstance().getBleDevice())) {
                    ToastCompat.show(getContext(), "蓝牙未连接");
                    return;
                }
                Class clazz = null;
                if (position == 0) {
                    clazz = FingerprintOpenLockActivity.class;
                } else if (position == 1) {
                    clazz = PasswordOpenLockActivity.class;
                } else if (position == 2) {
                    clazz = CardOpenLockActivity.class;
                }
                Intent intent = new Intent(OpenLockManagerActivity.this, clazz);
                startActivity(intent);
            }
        });
    }

    protected void loadData() {
        int fingerprintCount = 0;
        int passwordCount = 0;
        int cardCount = 0;
        
        int groupType = LockBLEManager.GROUP_TYPE == LockBLEManager.GROUP_HIJACK ? LockBLEManager.ALARM_TYPE : LockBLEManager.NORMAL_TYPE;
        String key = "locker_count_" + lockInfo.getId() + groupType;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            fingerprintCount = countInfo.getFingerprintCount();
            passwordCount = countInfo.getPasswordCount();
            cardCount = countInfo.getCardCount();
        }

        List<OpenLockTypeInfo> openLockTypeInfos = new ArrayList<>();

        OpenLockTypeInfo fingerprintOpenLockTypeInfo = new OpenLockTypeInfo();
        fingerprintOpenLockTypeInfo.setIcon(R.mipmap.icon_fingerprint);
        fingerprintOpenLockTypeInfo.setName("指纹");
        fingerprintOpenLockTypeInfo.setDesp(fingerprintCount + "个指纹");
        openLockTypeInfos.add(fingerprintOpenLockTypeInfo);

        OpenLockTypeInfo passwordOpenLockTypeInfo = new OpenLockTypeInfo();
        passwordOpenLockTypeInfo.setIcon(R.mipmap.icon_serct);
        passwordOpenLockTypeInfo.setName("密码");
        passwordOpenLockTypeInfo.setDesp(passwordCount + "个密码");
        openLockTypeInfos.add(passwordOpenLockTypeInfo);

        OpenLockTypeInfo cardOpenLockTypeInfo = new OpenLockTypeInfo();
        cardOpenLockTypeInfo.setIcon(R.mipmap.icon_nfc);
        cardOpenLockTypeInfo.setName("NFC门卡");
        cardOpenLockTypeInfo.setDesp(cardCount + "个门卡");
        openLockTypeInfos.add(cardOpenLockTypeInfo);

        openLockAdapter.setNewInstance(openLockTypeInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockCountRefreshEvent object) {
        loadData();
    }

    protected class OpenLockTypeInfo {
        private int icon;
        private String name;
        private String desp;

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesp() {
            return desp;
        }

        public void setDesp(String desp) {
            this.desp = desp;
        }
    }

    protected class OpenLockAdapter extends BaseExtendAdapter<OpenLockTypeInfo> {

        public OpenLockAdapter(@Nullable List<OpenLockTypeInfo> data) {
            super(R.layout.lock_ble_item_open_lock_type, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, OpenLockTypeInfo openLockTypeInfo) {
            Glide.with(OpenLockManagerActivity.this).load(openLockTypeInfo.getIcon()).into((ImageView) holder.getView(R.id.iv_icon));
            holder.setText(R.id.tv_name, openLockTypeInfo.getName());
            holder.setText(R.id.tv_desp, openLockTypeInfo.getDesp());
            if (holder.getAdapterPosition() == getData().size() - 1) {
                holder.setVisible(R.id.view_line, false);
            } else {
                holder.setVisible(R.id.view_line, true);
            }
        }
    }

}
