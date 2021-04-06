package com.yc.yfiotlock.controller.activitys.lock.remote;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.PasswordModifyOpenLockActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.ItemInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PasswordInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.view.adapters.ItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class TempPwdDetailActivity extends BaseBackActivity {

    @BindView(R.id.rv_temp_pwd_detail)
    RecyclerView recyclerView;
    @BindView(R.id.stv_del)
    SuperTextView delTv;

    private ItemAdapter itemAdapter;
    private LockEngine lockEngine;
    private PasswordInfo passWordInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_pwd_detail;
    }

    @Override
    protected void initVars() {
        super.initVars();

        lockEngine = new LockEngine(this);
        passWordInfo = (PasswordInfo) getIntent().getSerializableExtra("password_info");
    }

    @Override
    protected void initViews() {
        super.initViews();
        if (passWordInfo != null) {
            RxView.clicks(delTv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                GeneralDialog generalDialog = new GeneralDialog(TempPwdDetailActivity.this);
                generalDialog.setTitle("温馨提示");
                generalDialog.setMsg("是否删除" + passWordInfo.getName());
                generalDialog.setOnPositiveClickListener(dialog -> cloudDel());
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
        itemAdapter.setOnItemClickListener((adapter, view, position) -> {
            ItemInfo itemInfo = itemAdapter.getItem(position);
            OpenLockInfo openLockInfo = new OpenLockInfo();
            openLockInfo.setId(itemInfo.getId());
            openLockInfo.setName(itemInfo.getName());
            PasswordModifyOpenLockActivity.start(getContext(), openLockInfo);
        });
    }

    private void loadData() {
        List<ItemInfo> list = new ArrayList<>();
        if (passWordInfo != null) {
            list.add(new ItemInfo(passWordInfo.getName(), "", passWordInfo.getId()));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPwdChanged(OpenLockInfo openLockInfo) {
        if (openLockInfo.getId() == passWordInfo.getId()) {
            itemAdapter.getData().get(0).setName(openLockInfo.getName());
            itemAdapter.notifyItemChanged(0);
        }
    }
}
