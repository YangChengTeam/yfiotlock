package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public abstract class BaseModifyLockActivity extends BaseBackActivity {

    @BindView(R.id.et_name)
    EditText nameEt;
    @BindView(R.id.stv_commit)
    View commitBtn;

    protected OpenLockInfo openLockInfo;
    private LockEngine lockEngine;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_baes_modify_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
        openLockInfo = (OpenLockInfo) getIntent().getSerializableExtra("openlockinfo");
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (nameEt.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        RxView.clicks(commitBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (nameEt.length() < 2) {
                ToastUtil.toast2(getContext(), "名称长度不能少于2位");
                return;
            }
            cloudEdit();
        });

        setInfo();
    }

    private void setInfo() {
        nameEt.setText(openLockInfo.getName());
        nameEt.setSelection(nameEt.getText().length());
    }

    protected void cloudEdit() {
        mLoadingDialog.show("修改中...");
        String name = nameEt.getText().toString();
        openLockInfo.setName(name);
        lockEngine.modifyOpenLockName(openLockInfo.getId(), name).subscribe(new Subscriber<ResultInfo<String>>() {
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
                    finish();
                    EventBus.getDefault().post(openLockInfo);
                }
            }
        });
    }
}
