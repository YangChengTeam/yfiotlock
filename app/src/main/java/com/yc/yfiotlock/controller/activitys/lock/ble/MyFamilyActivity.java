package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.Dialog;
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
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.model.bean.eventbus.FamilyAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.engin.HomeEngine;
import com.yc.yfiotlock.view.adapters.MyFamilyAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observer;

public class MyFamilyActivity extends BaseBackActivity {

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
    protected void initVars() {
        super.initVars();

        homeEngine = new HomeEngine(this);
    }

    @Override
    protected void initViews() {
        super.initViews();

        initRv();

        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            loadData();
        });

        RxView.clicks(stvAdd).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyAddActivity.start(MyFamilyActivity.this, null);
        });

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
                        updateDefault(position);
                        break;
                    case R.id.tv_family_delete:
                        delete(position);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void delete(int position) {
        FamilyInfo familyInfo = myFamilyAdapter.getData().get(position);
        GeneralDialog generalDialog = new GeneralDialog(MyFamilyActivity.this);
        generalDialog.setTitle("提示");
        generalDialog.setMsg("确定删除家庭" + familyInfo.getName() + "?");
        generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                mLoadingDialog.show("删除中...");
                homeEngine.deleteFamily(familyInfo.getId()).subscribe(new Observer<ResultInfo<String>>() {
                    @Override
                    public void onCompleted() {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingDialog.dismiss();
                        ToastCompat.show(getContext(), e.getMessage());
                    }

                    @Override
                    public void onNext(ResultInfo<String> info) {
                        if (info != null && info.getCode() == 1) {
                            myFamilyAdapter.removeAt(position);
                        } else {
                            String msg = "更新出错";
                            msg = info != null && info.getMsg() != null ? info.getMsg() : msg;
                            ToastCompat.show(getContext(), msg);
                        }
                    }
                });
            }
        });
        generalDialog.show();
    }

    private void updateDefault(int position) {
        FamilyInfo familyInfo = myFamilyAdapter.getData().get(position);
        if (familyInfo.isDefault()) {
            return;
        }
        mLoadingDialog.show("更新中...");
        homeEngine.setDefaultFamily(familyInfo.getId()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), e.getMessage());
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    for (int i = 0; i < myFamilyAdapter.getData().size(); i++) {
                        myFamilyAdapter.getData().get(i).setDefault(true);
                    }
                    familyInfo.setDefault(false);
                    myFamilyAdapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new IndexRefreshEvent());
                } else {
                    String msg = "更新出错";
                    msg = info != null && info.getMsg() != null ? info.getMsg() : msg;
                    ToastCompat.show(getContext(), msg);
                }
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
                fail();
            }

            @Override
            public void onNext(ResultInfo<List<FamilyInfo>> info) {
                if (info != null && info.getCode() == 1) {
                    if (info.getData() == null || info.getData().size() == 0) {
                        empty();
                        return;
                    }
                    List<FamilyInfo> data = info.getData();
                    myFamilyAdapter.setNewInstance(data);
                } else {
                    fail();
                }
            }
        });
    }

    @Override
    public void fail() {
        super.fail();
        if (myFamilyAdapter.getData().size() == 0) {
            myFamilyAdapter.setNewInstance(null);
            myFamilyAdapter.setEmptyView(new NoWifiView(getContext()));
        }
    }

    @Override
    public void empty() {
        myFamilyAdapter.setNewInstance(null);
        myFamilyAdapter.setEmptyView(new NoDataView(getContext()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFamilyInfo(FamilyAddEvent event) {
        FamilyInfo familyInfo = event.getFamilyInfo();
        List<FamilyInfo> data = myFamilyAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == familyInfo.getId()) {
                myFamilyAdapter.setData(i, familyInfo);
                return;
            }
        }
        myFamilyAdapter.addData(0, familyInfo);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(0);
            }
        }, 500);
    }
}
