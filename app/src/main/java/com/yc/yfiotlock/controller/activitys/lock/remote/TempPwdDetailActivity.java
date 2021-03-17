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
import com.yc.yfiotlock.model.bean.lock.remote.NextTextInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.view.adapters.NextTextExtendAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TempPwdDetailActivity extends BaseActivity {

    public static void start(Context context, PassWordInfo passWordInfo) {
        Intent intent = new Intent(context, TempPwdDetailActivity.class);
        intent.putExtra("password_info", passWordInfo);
        context.startActivity(intent);
    }

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_temp_pwd_detail)
    RecyclerView recyclerView;

    private NextTextExtendAdapter nextTextExtendAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_pwd_detail;
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
                Serializable serializable = getIntent().getSerializableExtra("password_info");
                if (serializable instanceof PassWordInfo) {
                    CreatPwdActivity.start(TempPwdDetailActivity.this, (PassWordInfo) serializable);
                }
            }
        });
    }

    private void loadData() {
        List<NextTextInfo> list = new ArrayList<>();
        Serializable serializable = getIntent().getSerializableExtra("password_info");
        if (serializable instanceof PassWordInfo) {
            PassWordInfo passWordInfo = (PassWordInfo) serializable;

            mBnbTitle.setTitle(passWordInfo.getName());

            list.add(new NextTextInfo("修改密码", "", passWordInfo.getId()));
            nextTextExtendAdapter.setNewInstance(list);
        }

    }
}
