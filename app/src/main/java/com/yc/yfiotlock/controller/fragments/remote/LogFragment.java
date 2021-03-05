package com.yc.yfiotlock.controller.fragments.remote;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.lock.remote.OpenLockActivty;
import com.yc.yfiotlock.controller.activitys.lock.remote.TempPasswordOpenLockActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.LogInfo;
import com.yc.yfiotlock.model.bean.NextTextInfo;
import com.yc.yfiotlock.view.adapters.LogAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LogFragment extends BaseFragment {

    private LogAdapter logAdapter;
    @BindView(R.id.rv_log)
    RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_log;
    }

    @Override
    protected void initViews() {
        initRv();

        loadData();
    }


    private void initRv() {
        logAdapter = new LogAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(logAdapter);

        logAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {

            }
        });
    }


    private void loadData() {
        List<LogInfo> logInfoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LogInfo logInfo = new LogInfo("密码开门成功", "2020-02-05 16:00:00", "张三", R.mipmap.icon_log, i);
            logInfoList.add(logInfo);
        }
        logAdapter.setNewInstance(logInfoList);
    }
}
