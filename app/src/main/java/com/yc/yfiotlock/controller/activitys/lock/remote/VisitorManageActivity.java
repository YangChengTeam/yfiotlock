package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.lock.remote.NextTextInfo;
import com.yc.yfiotlock.view.adapters.NextTextExtendAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class VisitorManageActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.visitor_manage_recycler_view)
    RecyclerView recyclerView;

    private NextTextExtendAdapter nextTextExtendAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_manage;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        loadData();
    }


    private void initRv() {
        nextTextExtendAdapter = new NextTextExtendAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(nextTextExtendAdapter);

        nextTextExtendAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                NextTextInfo nextTextInfo = nextTextExtendAdapter.getData().get(position);
                switch (nextTextInfo.getId()) {
                    case 1:
                        startActivity(new Intent(VisitorManageActivity.this, OpenLockActivty.class));
                        break;
                    case 2:
                        startActivity(new Intent(VisitorManageActivity.this, TempPasswordOpenLockActivity.class));
                        break;
                }
            }
        });
    }

    private void loadData() {
        List<NextTextInfo> list = new ArrayList<>();
        list.add(new NextTextInfo("远程开锁", "", 1));
        list.add(new NextTextInfo("临时密码", "", 2));
        nextTextExtendAdapter.setNewInstance(list);
    }
}
