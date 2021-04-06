package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.BaseAddOpenLockActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.helper.TOTP;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.TimeInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PasswordInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.adapters.TempPwdAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

public class TempPasswordOpenLockActivity extends BaseBackActivity {
    @BindView(R.id.view_no_data)
    NoDataView nodataView;
    @BindView(R.id.view_no_wifi)
    NoWifiView noWifiView;
    @BindView(R.id.rv_temp_pwd)
    RecyclerView recyclerView;
    @BindView(R.id.stv_add)
    SuperTextView stvAdd;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private TempPwdAdapter tempPwdAdapter;
    private OpenLockDao openLockDao;
    private LockEngine lockEngine;
    private DeviceEngin deviceEngin;
    DeviceInfo lockInfo;
    protected int type = 2;

    private String key = "3132333435363738393031323334353637383930"
            + "313233343536373839303132";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temp_password_open_lock;
    }

    @Override
    protected void initVars() {
        super.initVars();
        openLockDao = App.getApp().getDb().openLockDao();
        lockEngine = new LockEngine(this);
        deviceEngin = new DeviceEngin(this);
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
        LockBLEManager.GROUP_TYPE = LockBLEManager.GROUP_TYPE_TEMP_PWD;
    }

    @Override
    protected void initViews() {
        setRv();
        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            synctimeLoadData();
        });

        mSrlRefresh.setRefreshing(true);
        synctimeLoadData();
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(R.id.stv_add, () -> {
            nav2add();
        });
    }

    private void setRv() {
        tempPwdAdapter = new TempPwdAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(tempPwdAdapter);
    }

    private void nav2add() {
        deviceEngin.getTime().subscribe(info -> {
            if (info != null && info.getCode() == 1 && info.getData() != null) {
                TimeInfo timeInfo = info.getData();
                String password = TOTP.generateTOTP256(key, Long.toHexString(timeInfo.getTime()).toUpperCase(), "6");
                localAdd(password, timeInfo.getTime() * 1000L);
            }
        });
    }

    private void synctimeLoadData() {
        deviceEngin.getTime().subscribe(info -> {
            if (info != null && info.getCode() == 1 && info.getData() != null) {
                TimeInfo timeInfo = info.getData();
                tempPwdAdapter.setSynctime(timeInfo.getTime() * 1000L);
                localLoadData();
            }
        });
    }

    private void localLoadData() {
        openLockDao.loadOpenLockInfos(lockInfo.getId(), type, LockBLEManager.GROUP_TYPE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                tempPwdAdapter.setNewInstance(openLockInfos);
                cloudLoadData();
            }
        });
    }

    private void cloudLoadData() {
        nodataView.setVisibility(View.GONE);
        noWifiView.setVisibility(View.GONE);
        lockEngine.getOpenLockWayList(lockInfo.getId() + "", type + "", LockBLEManager.GROUP_TYPE + "").subscribe(new Subscriber<ResultInfo<List<OpenLockInfo>>>() {
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
            public void onNext(ResultInfo<List<OpenLockInfo>> info) {
                if (info.getCode() == 1 && info.getData() != null) {
                    if (info.getData() == null || info.getData().size() == 0) {
                        empty();
                    } else {
                        success(info.getData());
                    }
                } else {
                    fail();
                }
            }
        });
    }

    @Override
    public void empty() {
        if (tempPwdAdapter.getData().size() == 0) {
            nodataView.setVisibility(View.VISIBLE);
            nodataView.setMessage("暂无临时密码数据");
        }
    }

    @Override
    public void fail() {
        if (tempPwdAdapter.getData().size() == 0) {
            noWifiView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void success(Object data) {
        List<OpenLockInfo> lastSyncLockInfos = new ArrayList<>();
        List<OpenLockInfo> lockInfos = (List<OpenLockInfo>) data;
        if (lockInfos.size() > 0) {
            lastSyncLockInfos.addAll(lockInfos);
            for (OpenLockInfo lopenLockInfo : tempPwdAdapter.getData()) {
                boolean isExist = false;
                for (OpenLockInfo copenLockInfo : lockInfos) {
                    if (copenLockInfo.getKeyid() == lopenLockInfo.getKeyid()) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    lastSyncLockInfos.add(lopenLockInfo);
                }
            }
            tempPwdAdapter.setNewInstance(lastSyncLockInfos);
        }
    }


    protected void localAdd(String keyid, long time) {
        String name = "临时密码";
        localAdd(name, LockBLEManager.OPEN_LOCK_PASSWORD, Integer.valueOf(keyid), keyid + "", time);
    }

    protected void localAdd(String name, int type, int keyid, String password, long time) {
        OpenLockInfo openLockInfo = new OpenLockInfo();
        openLockInfo.setKeyid(keyid);
        openLockInfo.setName(name);
        openLockInfo.setType(type);
        openLockInfo.setAddtime(time);
        openLockInfo.setLockId(lockInfo.getId());
        openLockInfo.setPassword(password);
        openLockInfo.setAddUserMobile("我");
        openLockInfo.setGroupType(LockBLEManager.GROUP_TYPE);
        openLockDao.insertOpenLockInfo(openLockInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                localAddSucc();
                if (CommonUtil.isNetworkAvailable(getContext())) {
                    cloudAdd(name, type, keyid, password);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (retryCount-- > 3) {
                    localAdd(name, type, keyid, password, time);
                } else {
                    retryCount = 3;
                    cloudAdd(name, type, keyid, password, true);
                }
            }
        });
    }

    protected void cloudAdd(String name, int type, int keyid, String password) {
        cloudAdd(name, type, keyid, password, false);
    }

    protected void cloudAdd(String name, int type, int keyid, String password, boolean isRetry) {
        mLoadingDialog.show("添加中...");
        lockEngine.addOpenLockWay(lockInfo.getId() + "", name, keyid + "", type, LockBLEManager.GROUP_TYPE + "", password).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                if (!isRetry) {
                    mLoadingDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!isRetry) {
                    mLoadingDialog.dismiss();
                }
                localAddSucc();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    mLoadingDialog.dismiss();
                    openLockDao.updateOpenLockInfo(lockInfo.getId(), keyid, true).subscribeOn(Schedulers.io()).subscribe();
                } else {
                    if (isRetry) {
                        fail(name, type, keyid, password);
                    }
                }
            }
        });
    }

    public void fail(String name, int type, int keyid, String password) {
        if (retryCount-- > 0) {
            VUiKit.postDelayed(retryCount * (1000 - retryCount * 200), () -> {
                cloudAdd(name, type, keyid, password, true);
            });
        } else {
            retryCount = 3;
            mLoadingDialog.dismiss();
            GeneralDialog generalDialog = new GeneralDialog(getContext());
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("同步云端失败, 请重试");
            generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    mLoadingDialog.show("添加中...");
                    cloudAdd(name, type, keyid, password, true);
                }
            });
            generalDialog.show();
        }
    }

    private void localAddSucc() {
        synctimeLoadData();
    }


}
