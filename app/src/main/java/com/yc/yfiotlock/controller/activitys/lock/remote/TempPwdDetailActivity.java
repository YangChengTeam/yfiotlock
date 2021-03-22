package com.yc.yfiotlock.controller.activitys.lock.remote;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.BaseDetailOpenLockActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.PasswordModifyOpenLockActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.remote.ItemInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PassWordInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.view.adapters.ItemAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class TempPwdDetailActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.rv_temp_pwd_detail)
    RecyclerView recyclerView;
    @BindView(R.id.stv_del)
    SuperTextView delTv;

    private ItemAdapter itemAdapter;
    private LockEngine lockEngine;
    private PassWordInfo passWordInfo;

    public static void start(Context context, PassWordInfo passWordInfo) {
        Intent intent = new Intent(context, TempPwdDetailActivity.class);
        intent.putExtra("password_info", passWordInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_pwd_detail;
    }

    @Override
    protected void initVars() {
        super.initVars();

        lockEngine = new LockEngine(this);
        passWordInfo = (PassWordInfo) getIntent().getSerializableExtra("password_info");
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        if (passWordInfo != null) {
            RxView.clicks(delTv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                GeneralDialog generalDialog = new GeneralDialog(TempPwdDetailActivity.this);
                generalDialog.setTitle("温馨提示");
                generalDialog.setMsg("是否删除" + passWordInfo.getName());
                generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        cloudDel();
                    }
                });
                generalDialog.show();
            });
        }

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
                if (passWordInfo != null) {
                    Intent intent = new Intent(TempPwdDetailActivity.this, PasswordModifyOpenLockActivity.class);
                    intent.putExtra("password_info", passWordInfo);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadData() {
        List<ItemInfo> list = new ArrayList<>();
        if (passWordInfo != null) {
            mBnbTitle.setTitle(passWordInfo.getName());

            list.add(new ItemInfo("修改名称", "", passWordInfo.getId()));
            itemAdapter.setNewInstance(list);
        }
    }

    protected void cloudDel() {
        mLoadingDialog.show("删除中...");
        lockEngine.delOpenLockWay(passWordInfo.getId() + "").subscribe(new Subscriber<ResultInfo<String>>() {
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
                if (stringResultInfo.getCode() == 1) {

                    ToastUtil.toast2(TempPwdDetailActivity.this, "删除成功");
                    EventBus.getDefault().post(new OpenLockRefreshEvent());

                    finish();
                }
            }
        });
    }
}
