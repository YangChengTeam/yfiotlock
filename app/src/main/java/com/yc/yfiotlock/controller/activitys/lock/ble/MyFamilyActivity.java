package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.TempPasswordOpenLockActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.TempPwdDetailActivity;
import com.yc.yfiotlock.model.bean.FamilyInfo;
import com.yc.yfiotlock.model.bean.PassWordInfo;
import com.yc.yfiotlock.view.adapters.MyFamilyAdapter;
import com.yc.yfiotlock.view.adapters.TempPwdAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyFamilyActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_temp_pwd)
    RecyclerView recyclerView;
    private MyFamilyAdapter myFamilyAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_my_family;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        loadData();
    }

    private void initRv() {
        myFamilyAdapter = new MyFamilyAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myFamilyAdapter);

        myFamilyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
//                TempPwdDetailActivity.start(MyFamilyActivity.this, myFamilyAdapter.getItem(position));
            }
        });
    }

    private void loadData() {
        List<FamilyInfo> list = new ArrayList<>();
        list.add(new FamilyInfo("我的家", "创意天地", "5号楼8楼", false, 5, 1));
        list.add(new FamilyInfo("我的家2", "创意天地", "5号楼8楼", true, 2, 2));
        myFamilyAdapter.setNewInstance(list);
    }
}
