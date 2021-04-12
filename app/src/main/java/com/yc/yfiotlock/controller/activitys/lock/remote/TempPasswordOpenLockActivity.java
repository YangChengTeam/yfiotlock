package com.yc.yfiotlock.controller.activitys.lock.remote;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.helper.TOTP;
import com.yc.yfiotlock.model.bean.eventbus.CloudAddEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.TimeInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.adapters.TempPwdAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import org.greenrobot.eventbus.EventBus;

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
    private DeviceInfo lockInfo;
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
        super.initViews();

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
            } else {
                tempPwdAdapter.setSynctime(System.currentTimeMillis());
            }
            localLoadData();
        });
    }

    private void localLoadData() {
        nodataView.setVisibility(View.GONE);
        noWifiView.setVisibility(View.GONE);
        mSrlRefresh.setRefreshing(true);
        openLockDao.loadOpenLockInfos(lockInfo.getId(), type, LockBLEManager.GROUP_TYPE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<OpenLockInfo>>() {
            @Override
            public void accept(List<OpenLockInfo> openLockInfos) throws Exception {
                tempPwdAdapter.setNewInstance(openLockInfos);
                if (CommonUtil.isNetworkAvailable(getContext()) && openLockInfos.size() == 0) {
                    cloudLoadData();
                } else {
                    mSrlRefresh.setRefreshing(false);
                    empty();
                }
            }
        });
    }

    private void cloudLoadData() {
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
                if (info != null && info.getCode() == 1) {
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
        List<OpenLockInfo> lockInfos = (List<OpenLockInfo>) data;
        tempPwdAdapter.setNewInstance(lockInfos);
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
        openLockInfo.setMasterLockId(lockInfo.getId());
        openLockInfo.setLockId(lockInfo.getId());
        openLockInfo.setPassword(password);
        openLockInfo.setAddUserMobile("我");
        openLockInfo.setGroupType(LockBLEManager.GROUP_TYPE);
        openLockDao.insertOpenLockInfo(openLockInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                retryCount = 3;
            }

            @Override
            public void onComplete() {
                localAddSucc();
                if (CommonUtil.isNetworkAvailable(getContext())) {
                    openLockInfo.setAdd(true);
                    EventBus.getDefault().post(new CloudAddEvent(openLockInfo));
                }
            }

            @Override
            public void onError(Throwable e) {
                if (retryCount-- > 3) {
                    localAdd(name, type, keyid, password, time);
                } else {
                    retryCount = 3;
                }
            }
        });
    }

    private void localAddSucc() {
        synctimeLoadData();
    }
}
