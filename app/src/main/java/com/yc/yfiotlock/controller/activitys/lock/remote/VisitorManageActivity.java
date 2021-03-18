package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.ItemInfo;
import com.yc.yfiotlock.view.adapters.ItemAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class VisitorManageActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.visitor_manage_recycler_view)
    RecyclerView recyclerView;

    private ItemAdapter itemAdapter;
    private DeviceInfo deviceInfo;

    public static void start(Context context, DeviceInfo deviceInfo) {
        Intent intent = new Intent(context, VisitorManageActivity.class);
        intent.putExtra("device", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_manage;
    }

    @Override
    protected void initVars() {
        super.initVars();
        Serializable device = getIntent().getSerializableExtra("device");
        if (device instanceof DeviceInfo) {
            this.deviceInfo = (DeviceInfo) device;
        }
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        loadData();
    }


    private void initRv() {
        itemAdapter = new ItemAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                ItemInfo itemInfo = itemAdapter.getData().get(position);
                switch (itemInfo.getId()) {
                    case 1:
                        OpenLockActivty.start(VisitorManageActivity.this, deviceInfo);
                        break;
                    case 2:
                        startActivity(new Intent(VisitorManageActivity.this, TempPasswordOpenLockActivity.class));
                        break;
                }
            }
        });
    }

    private void loadData() {
        List<ItemInfo> list = new ArrayList<>();
        list.add(new ItemInfo("远程开锁", "", 1));
        list.add(new ItemInfo("临时密码", "", 2));
        itemAdapter.setNewInstance(list);
    }
}
