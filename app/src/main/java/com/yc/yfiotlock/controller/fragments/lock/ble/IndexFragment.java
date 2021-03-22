package com.yc.yfiotlock.controller.fragments.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.add.ScanDeviceActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.model.engin.IndexEngin;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.view.adapters.IndexDeviceAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class IndexFragment extends BaseFragment {


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

        loadData();
    }

    @Override
    protected void bindClick() {
        setClick(R.id.tv_my_family, () -> {
            //            nav2MyFamily();

            nav2LockIndex(new DeviceInfo());
        });

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
                nav2LockIndex((DeviceInfo) adapter.getData().get(position));
            }
        });
    }

    private void loadData() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        IndexInfo indexInfo = CacheUtil.getCache(Config.INDEX_DETAIL_URL, IndexInfo.class);
        if (indexInfo != null) {
            familyInfo = indexInfo.getFamilyInfo();
            indexDeviceAdapter.setNewInstance(indexInfo.getDeviceInfos());
        } else {
            baseActivity.mLoadingDialog.show("加载中...");
        }
        indexEngin.getIndexInfo().subscribe(new Subscriber<ResultInfo<IndexInfo>>() {
            @Override
            public void onCompleted() {
                baseActivity.mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                baseActivity.mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<IndexInfo> resultInfo) {
                if (resultInfo != null && resultInfo.getCode() == 1 && resultInfo.getData() != null) {
                    familyInfo = resultInfo.getData().getFamilyInfo();
                    List<DeviceInfo> deviceInfoList = resultInfo.getData().getDeviceInfos();
                    if (deviceInfoList == null) {
                        deviceInfoList = new ArrayList<>();
                    }
                    deviceInfoList.add(new DeviceInfo());
                    indexDeviceAdapter.setNewInstance(deviceInfoList);
                    CacheUtil.setCache(Config.INDEX_DETAIL_URL, resultInfo.getData());
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
        Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
        intent.putExtra("family", familyInfo);
        startActivity(intent);
    }

}
