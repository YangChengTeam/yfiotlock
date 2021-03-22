package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyAddActivity;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.view.adapters.TempPwdAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class TempPasswordOpenLockActivity extends BaseActivity {
    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_temp_pwd)
    RecyclerView recyclerView;
    @BindView(R.id.stv_add)
    SuperTextView stvAdd;

    private TempPwdAdapter tempPwdAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_password_open_lock;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        RxView.clicks(stvAdd).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            startActivity(new Intent(TempPasswordOpenLockActivity.this, CreatPwdActivity.class));
        });

        loadData();
    }

    private void initRv() {
        tempPwdAdapter = new TempPwdAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(tempPwdAdapter);

        tempPwdAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                TempPwdDetailActivity.start(TempPasswordOpenLockActivity.this, tempPwdAdapter.getItem(position));
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
