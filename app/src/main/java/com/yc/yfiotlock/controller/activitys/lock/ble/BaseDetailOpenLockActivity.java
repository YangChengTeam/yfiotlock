package com.yc.yfiotlock.controller.activitys.lock.ble;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.ble.BaseDetailOpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.ble.BaseOpenLockInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public abstract class BaseDetailOpenLockActivity extends BaseBackActivity {
    @BindView(R.id.rv_open_lock)
    RecyclerView openLockRecyclerView;
    @BindView(R.id.stv_del)
    SuperTextView delTv;

    protected OpenLockAdapter openLockAdapter;

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_base_detail_open_lock;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setNavTitle(title + "详情");
        delTv.setText(delTv.getText() + title);

        setRv();
    }

    private void setRv() {
        List<BaseDetailOpenLockInfo> openLockTypeInfos = new ArrayList<>();
        openLockTypeInfos.add(new BaseDetailOpenLockInfo());
        openLockAdapter = new OpenLockAdapter(openLockTypeInfos);
        openLockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openLockRecyclerView.setAdapter(openLockAdapter);
    }

    public static class OpenLockAdapter extends BaseExtendAdapter<BaseDetailOpenLockInfo> {
        public OpenLockAdapter(@Nullable List<BaseDetailOpenLockInfo> data) {
            super(R.layout.lock_ble_item_base_open_lock, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, BaseDetailOpenLockInfo openLockTypeInfo) {
            holder.setText(R.id.tv_name, "修改名称");
            if (holder.getAdapterPosition() == getData().size() - 1) {
                holder.setVisible(R.id.view_line, false);
            } else {
                holder.setVisible(R.id.view_line, true);
            }
        }
    }
}
