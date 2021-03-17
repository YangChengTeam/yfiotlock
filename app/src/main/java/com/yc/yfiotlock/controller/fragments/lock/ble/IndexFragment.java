package com.yc.yfiotlock.controller.fragments.lock.ble;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.AddDeviceActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.engin.IndexEngin;
import com.yc.yfiotlock.view.adapters.IndexDeviceAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;
import rx.functions.Action1;

public class IndexFragment extends BaseFragment {

    @BindView(R.id.ll_my_family)
    View myFamilyBtn;
    @BindView(R.id.iv_device_add)
    View deviceAddBtn;

    @BindView(R.id.rv_devices)
    RecyclerView devicesRecyclerView;

    private IndexDeviceAdapter indexDeviceAdapter;
    private IndexEngin indexEngin;

    private FamilyInfo familyInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_fragment_index;
    }

    @Override
    protected void initVars() {
        super.initVars();
        indexEngin = new IndexEngin(getActivity());
    }

    @Override
    protected void initViews() {
        setRv();

        RxView.clicks(myFamilyBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2MyFamily();
        });

        RxView.clicks(deviceAddBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2AddDevice();
        });

        loadData();
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
                nav2LockIndex((DeviceInfo) adapter.getData().get(position));
            }
        });
    }

    private void loadData(){

    }

    private void loadDevices() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.mLoadingDialog.show("加载中...");
        indexEngin.getDeviceList().subscribe(new Subscriber<ResultInfo<List<DeviceInfo>>>() {
            @Override
            public void onCompleted() {
                baseActivity.mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                baseActivity.mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<List<DeviceInfo>> resultInfo) {
                if (resultInfo != null && resultInfo.getCode() == 1) {
                    indexDeviceAdapter.setNewInstance(resultInfo.getData());
                }
            }
        });
    }

    private void loadDefaultFamily() {
        indexEngin.getDefaultFamily().subscribe(new Action1<ResultInfo<FamilyInfo>>() {
            @Override
            public void call(ResultInfo<FamilyInfo> resultInfo) {
                if (resultInfo != null && resultInfo.getCode() == 1) {
                    familyInfo = resultInfo.getData();
                }
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
        Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
        startActivity(intent);
    }

}
