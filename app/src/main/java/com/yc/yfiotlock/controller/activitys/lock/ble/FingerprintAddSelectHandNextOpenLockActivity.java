package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.eventbus.CloudOpenLockUpdateEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FingerprintAddSelectHandNextOpenLockActivity extends BaseFingerprintAddOpenLockActivity {

    @BindViews({R.id.iv_finger1, R.id.iv_finger2, R.id.iv_finger3, R.id.iv_finger4, R.id.iv_finger5})
    View[] fingerBtns;

    @BindView(R.id.tv_hand)
    TextView nameTv;

    private String name;
    private int keyid;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_fingerprint_add_select_hand_next_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        name = getIntent().getStringExtra("name");
        keyid = getIntent().getIntExtra("keyid", 0);
    }

    @Override
    protected void initViews() {
        super.initViews();
        for (int i = 0; i < fingerBtns.length; i++) {
            final View fingerBtn = fingerBtns[i];
            RxView.clicks(fingerBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                name += fingerBtn.getTag() + "";
                localUpdate();
            });
        }
        nameTv.setText(name);
    }

    private void localUpdate() {
        openLockDao.updateOpenLockInfo(lockInfo.getId(), keyid, LockBLEManager.GROUP_TYPE, name).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                OpenLockInfo openLockInfo = new OpenLockInfo();
                openLockInfo.setLockId(lockInfo.getId());
                openLockInfo.setKeyid(keyid);
                openLockInfo.setName(name);
                openLockInfo.setGroupType(LockBLEManager.GROUP_TYPE);
                EventBus.getDefault().post(new OpenLockRefreshEvent());
                EventBus.getDefault().post(new CloudOpenLockUpdateEvent(openLockInfo));
                mLoadingDialog.setIcon(R.mipmap.icon_finish);
                mLoadingDialog.show("更新成功");
                VUiKit.postDelayed(1500, new Runnable() {
                    @Override
                    public void run() {
                        if (CommonUtil.isActivityDestory(getContext())) {
                            return;
                        }
                        mLoadingDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                ToastCompat.show(getContext(), "更新失败, 请重试");
            }
        });
    }
}
