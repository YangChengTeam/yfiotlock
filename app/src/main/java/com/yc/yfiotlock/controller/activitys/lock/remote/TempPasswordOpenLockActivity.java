package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.PassWordInfo;
import com.yc.yfiotlock.view.adapters.TempPwdAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TempPasswordOpenLockActivity extends BaseActivity {
    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_temp_pwd)
    RecyclerView recyclerView;
    private TempPwdAdapter tempPwdAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_password_open_lock;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        loadData();
    }


    private void initRv() {
        tempPwdAdapter = new TempPwdAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(tempPwdAdapter);

        tempPwdAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {

            }
        });
    }

    private void loadData() {
        List<PassWordInfo> list = new ArrayList<>();
        list.add(new PassWordInfo("密码1", "一次性", 0, 1));
        list.add(new PassWordInfo("密码2", "一次性", 1, 2));
        list.add(new PassWordInfo("密码3", "一次性", 2, 3));
        tempPwdAdapter.setNewInstance(list);
    }
}
