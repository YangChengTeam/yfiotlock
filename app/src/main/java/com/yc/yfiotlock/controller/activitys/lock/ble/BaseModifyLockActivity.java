package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.model.bean.eventbus.CloudAddEvent;
import com.yc.yfiotlock.model.bean.eventbus.CloudUpdateEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

public abstract class BaseModifyLockActivity extends BaseBackActivity {

    @BindView(R.id.et_name)
    EditText nameEt;
    @BindView(R.id.stv_commit)
    View commitBtn;

    protected OpenLockDao openLockDao;
    protected OpenLockInfo openLockInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_baes_modify_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        openLockDao = App.getApp().getDb().openLockDao();
        openLockInfo = (OpenLockInfo) getIntent().getSerializableExtra("openlockinfo");
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (nameEt.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        RxView.clicks(commitBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (nameEt.length() < 2) {
                ToastUtil.toast2(getContext(), "名称长度不能少于2位");
                return;
            }
            localEdit();
        });

        setInfo();

        nameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (nameEt.length() < 2) {
                        ToastUtil.toast2(getContext(), "名称长度不能少于2位");
                        return false;
                    }
                    localEdit();
                }
                return false;
            }
        });
    }


    private void setInfo() {
        nameEt.setText(openLockInfo.getName());
        nameEt.setSelection(nameEt.getText().length());
    }

    protected void localEdit() {
        String name = nameEt.getText().toString();
        openLockDao.updateOpenLockInfo(openLockInfo.getLockId(), openLockInfo.getKeyid(), openLockInfo.getGroupType(), name).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                retryCount = 3;
                openLockInfo.setName(name);
                EventBus.getDefault().post(openLockInfo);
                if (CommonUtil.isNetworkAvailable(getContext())) {
                    EventBus.getDefault().post(new CloudUpdateEvent(openLockInfo));
                }
                EventBus.getDefault().post(new OpenLockRefreshEvent());
                finish();
            }

            @Override
            public void onError(Throwable e) {
                if (retryCount-- > 0) {
                    localEdit();
                } else {
                    retryCount = 3;
                }
            }
        });
    }
}
