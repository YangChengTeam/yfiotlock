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
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OpenLockManagerActivity extends BaseActivity {

    @BindView(R.id.rv_open_lock)
    RecyclerView openLockRecyclerView;

    OpenLockAdapter openLockAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_open_lock_manager;
    }

    @Override
    protected void initViews() {
        setRv();
    }

    private void setRv() {
        List<OpenLockTypeInfo> openLockTypeInfos = new ArrayList<>();

        OpenLockTypeInfo openLockTypeInfo = new OpenLockTypeInfo();
        openLockTypeInfo.setIcon(R.mipmap.icon_fingerprint);
        openLockTypeInfo.setName("指纹");
        openLockTypeInfo.setDesp("0个指纹");
        openLockTypeInfos.add(openLockTypeInfo);

        OpenLockTypeInfo openLockTypeInfo2 = new OpenLockTypeInfo();
        openLockTypeInfo2.setIcon(R.mipmap.icon_serct);
        openLockTypeInfo2.setName("密码");
        openLockTypeInfo2.setDesp("0个密码");
        openLockTypeInfos.add(openLockTypeInfo2);

        OpenLockTypeInfo openLockTypeInfo3 = new OpenLockTypeInfo();
        openLockTypeInfo3.setIcon(R.mipmap.icon_nfc);
        openLockTypeInfo3.setName("NFC门卡");
        openLockTypeInfo3.setDesp("0个门卡");
        openLockTypeInfos.add(openLockTypeInfo3);

        openLockAdapter = new OpenLockAdapter(openLockTypeInfos);
        openLockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openLockRecyclerView.setAdapter(openLockAdapter);
        openLockAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
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

    private class OpenLockTypeInfo {
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

    private class OpenLockAdapter extends BaseExtendAdapter<OpenLockTypeInfo> {

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
