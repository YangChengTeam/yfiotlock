package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.FamilyInfo;
import com.yc.yfiotlock.model.engin.HomeEngine;
import com.yc.yfiotlock.view.adapters.MyFamilyAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observer;

public class MyFamilyActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_my_family)
    RecyclerView recyclerView;
    @BindView(R.id.stv_add)
    SuperTextView stvAdd;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private MyFamilyAdapter myFamilyAdapter;
    private HomeEngine homeEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_my_family;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());
        initRv();

        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            loadData();
        });

        RxView.clicks(stvAdd).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyAddActivity.start(MyFamilyActivity.this, null);
        });

        homeEngine = new HomeEngine(this);

        mSrlRefresh.setRefreshing(true);
        loadData();
    }

    private void initRv() {
        myFamilyAdapter = new MyFamilyAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myFamilyAdapter);

        myFamilyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                MyFamilyAddActivity.start(MyFamilyActivity.this, myFamilyAdapter.getItem(position));
            }
        });

        myFamilyAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull @NotNull BaseQuickAdapter adapter, @NonNull @NotNull View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_family_number_default:
                    case R.id.tv_family_number_default:
                        checkDefault(position);
                        break;
                    case R.id.tv_family_delete:
                        delete(position);
                        break;
                }
            }

            private void delete(int position) {
                FamilyInfo familyInfo = myFamilyAdapter.getData().get(position);
                mLoadingDialog.show("处理中...");
                homeEngine.deleteFamily(familyInfo.getId()).subscribe(new Observer<ResultInfo<String>>() {
                    @Override
                    public void onCompleted() {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onNext(ResultInfo<String> stringResultInfo) {
                        myFamilyAdapter.removeAt(position);
                    }
                });
            }

            private void checkDefault(int position) {
                FamilyInfo familyInfo = myFamilyAdapter.getData().get(position);
                if (familyInfo.isIs_def() == 0) {
                    return;
                }
                mLoadingDialog.show("处理中...");
                homeEngine.setDefaultFamily(familyInfo.getId()).subscribe(new Observer<ResultInfo<String>>() {
                    @Override
                    public void onCompleted() {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onNext(ResultInfo<String> stringResultInfo) {
                        myFamilyAdapter.updateCheck(familyInfo);
                    }
                });
            }
        });
    }

    private void loadData() {
        homeEngine.getHomeList().subscribe(new Observer<ResultInfo<List<FamilyInfo>>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onNext(ResultInfo<List<FamilyInfo>> listResultInfo) {
                List<FamilyInfo> data = listResultInfo.getData();
                myFamilyAdapter.setNewInstance(data);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFamilyInfo(FamilyInfo familyInfo) {
        if (familyInfo.isUpdateList()) {
            familyInfo.setUpdateList(false);
            List<FamilyInfo> data = myFamilyAdapter.getData();
            int index = -1;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId() == familyInfo.getId()) {
                    index = i;
                }
            }
            if (index > 0) {
                myFamilyAdapter.setData(index, familyInfo);
            } else {
                myFamilyAdapter.addData(0, familyInfo);
            }
        }
    }
}
