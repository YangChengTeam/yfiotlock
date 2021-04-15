package com.yc.yfiotlock.controller.fragments.lock.ble;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.kk.utils.LogUtil;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.lock.ble.SafePwdCreateActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.add.ScanDeviceActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.IndexEngin;
import com.yc.yfiotlock.model.engin.ShareDeviceEngine;
import com.yc.yfiotlock.offline.OfflineManager;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.SafeUtil;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.adapters.IndexDeviceAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rx.Observer;
import rx.Subscriber;

import static android.app.Activity.RESULT_OK;

public class IndexFragment extends BaseFragment {
    @BindView(R.id.iv_device_add)
    View deviceAddBtn;

    @BindView(R.id.rv_devices)
    RecyclerView devicesRecyclerView;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;

    private IndexDeviceAdapter indexDeviceAdapter;
    private IndexEngin indexEngin;
    private DeviceDao deviceDao;

    private FamilyInfo familyInfo;
    private DeviceInfo mDeviceInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_fragment_index;
    }

    @Override
    protected void initVars() {
        super.initVars();
        mEngine = new ShareDeviceEngine(getContext());
        indexEngin = new IndexEngin(getActivity());
        deviceDao = App.getApp().getDb().deviceDao();
    }

    @Override
    protected void initViews() {
        setRv();
        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setProgressViewOffset(false, 0, ScreenUtil.dip2px(getContext(), 50));
        mSrlRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    @Override
    protected void bindClick() {
        setClick(R.id.tv_my_family, this::nav2MyFamily);
        setClick(deviceAddBtn, this::nav2AddDevice);
    }

    private void setRv() {
        indexDeviceAdapter = new IndexDeviceAdapter(null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        devicesRecyclerView.setLayoutManager(gridLayoutManager);
        devicesRecyclerView.setAdapter(indexDeviceAdapter);

        indexDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == adapter.getData().size() - 1) {
                nav2AddDevice();
            } else {
                mDeviceInfo = (DeviceInfo) adapter.getData().get(position);
                if (mDeviceInfo.isShare()) {
                    checkLockExist();
                } else {
                    nav2LockIndex();
                }
            }
        });
    }

    private ShareDeviceEngine mEngine;

    private void checkLockExist() {
        mLoadingDialog.show("设备校验中...");
        String msg = "校验失败";
        mEngine.checkLockExist(mDeviceInfo.getId() + "").subscribe(new Observer<ResultInfo<DeviceInfo>>() {
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
            public void onNext(ResultInfo<DeviceInfo> info) {
                if (info != null && info.getData() != null && info.getData().isValid()) {
                    nav2LockIndex();
                } else {
                    String tmsg = msg;
                    tmsg = info != null && info.getMsg() != null ? info.getMsg() : tmsg;
                    ToastCompat.show(getContext(), tmsg);
                    loadData();
                }
            }
        });
    }

    private void processData(List<DeviceInfo> deviceInfos) {
        for (DeviceInfo deviceInfo : deviceInfos) {
            deviceInfo.setFamilyId(familyInfo.getId());
            deviceInfo.setMasterId(UserInfoCache.getUserInfo().getId());
        }
    }

    private void loadData() {
        IndexInfo indexInfo = CacheUtil.getCache(Config.INDEX_DETAIL_URL, IndexInfo.class);
        if (indexInfo != null) {
            familyInfo = indexInfo.getFamilyInfo();
            indexDeviceAdapter.setNewInstance(indexInfo.getDeviceInfos());
        } else {
            mSrlRefresh.setRefreshing(true);
        }
        indexEngin.getIndexInfo().subscribe(new Subscriber<ResultInfo<IndexInfo>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onNext(ResultInfo<IndexInfo> resultInfo) {
                if (resultInfo != null && resultInfo.getCode() == 1 && resultInfo.getData() != null) {
                    familyInfo = resultInfo.getData().getFamilyInfo();
                    localLoadData(resultInfo.getData());
                    OfflineManager.enqueue(getContext());
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void localLoadData(IndexInfo indexInfo) {
        List<DeviceInfo> cDeviceInfos = indexInfo.getDeviceInfos();
        deviceDao.loadDeviceInfo(familyInfo.getId()).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<DeviceInfo>>() {
            @Override
            public void accept(List<DeviceInfo> lDeviceInfos) throws Exception {
                HashMap<String, DeviceInfo> hashMap = new HashMap<>();
                List<DeviceInfo> lastDeviceInfos;
                if (lDeviceInfos == null || lDeviceInfos.size() == 0) {
                    lastDeviceInfos = cDeviceInfos;
                } else {

                    for (DeviceInfo cDeviceInfo : cDeviceInfos) {
                        cDeviceInfo.setAdd(true);
                        hashMap.put(cDeviceInfo.getMacAddress(), cDeviceInfo);
                    }

                    for (DeviceInfo lDeviceInfo : lDeviceInfos) {
                        if (lDeviceInfo.isDelete()) {
                            if (hashMap.get(lDeviceInfo.getMacAddress()) != null) {
                                hashMap.remove(lDeviceInfo.getMacAddress());
                            }
                        } else if (hashMap.get(lDeviceInfo.getMacAddress()) == null && !lDeviceInfo.isShare()) {
                            hashMap.put(lDeviceInfo.getMacAddress(), lDeviceInfo);
                        }
                    }

                    lastDeviceInfos = new ArrayList<>();
                    List<DeviceInfo> finalLastDeviceInfos = lastDeviceInfos;
                    hashMap.forEach((k, v) -> {
                        finalLastDeviceInfos.add(v);
                    });

                }
                processData(lastDeviceInfos);
                deviceDao.insertOpenLockInfos(lastDeviceInfos).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        lastDeviceInfos.add(new DeviceInfo());

                        indexDeviceAdapter.setNewInstance(lastDeviceInfos);
                        indexInfo.setDeviceInfos(lastDeviceInfos);
                        CacheUtil.setCache(Config.INDEX_DETAIL_URL, indexInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(IndexRefreshEvent object) {
        loadData();
    }

    private void nav2MyFamily() {
        Intent intent = new Intent(getActivity(), MyFamilyActivity.class);
        startActivity(intent);
    }

    private void nav2LockIndex() {
        switch (SafeUtil.getSafePwdType(mDeviceInfo)) {
            case SafeUtil.NO_PASSWORD:
                nav2LockIndex(mDeviceInfo);
                break;
            case SafeUtil.PASSWORD_TYPE:
                SafePwdCreateActivity.startCheck(getActivity());
                break;
            case SafeUtil.FINGERPRINT_TYPE:
                checkFinger();
                break;
            default:
                break;
        }
    }

    private void checkFinger() {
        SafeUtil.useFinger(getActivity(), new Callback<String>() {
            @Override
            public void onSuccess(String resultInfo) {
                nav2LockIndex(mDeviceInfo);
            }

            @Override
            public void onFailure(Response response) {

            }
        });
    }

    private void nav2LockIndex(DeviceInfo deviceInfo) {
        Intent intent = new Intent(getActivity(), LockIndexActivity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("device", deviceInfo);
        startActivity(intent);
    }

    private void nav2AddDevice() {
        if (familyInfo == null) {
            ToastCompat.show(getActivity(), "家庭信息未加载");
            return;
        }
        Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
        intent.putExtra("family", familyInfo);
        startActivity(intent);
    }

    public static final int CHECK_PWD = 111;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_PWD) {
            if (resultCode == RESULT_OK && data != null
                    && SafeUtil.getSafePwd(mDeviceInfo).equals(data.getStringExtra("pwd"))) {
                nav2LockIndex(mDeviceInfo);
            } else {
                ToastCompat.show(getContext(), "密码错误");
            }
        }
    }
}
