package com.yc.yfiotlock.controller.fragments.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.IMyAidlInterface;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.lock.ble.AddDeviceActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.view.adapters.IndexDeviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class IndexFragment extends BaseFragment {

    @BindView(R.id.ll_my_family)
    View myFamilyBtn;
    @BindView(R.id.iv_device_add)
    View deviceAddBtn;

    @BindView(R.id.rv_devices)
    RecyclerView devicesRecyclerView;

    IndexDeviceAdapter indexDeviceAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_fragment_index;
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
    }

    private void setRv() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        deviceInfos.add(new DeviceInfo());
        deviceInfos.add(new DeviceInfo());

        indexDeviceAdapter = new IndexDeviceAdapter(deviceInfos);
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

    private void nav2MyFamily() {
        Intent intent = new Intent(getActivity(), MyFamilyActivity.class);
        startActivity(intent);
    }

    private void nav2LockIndex(DeviceInfo deviceInfo) {
        Intent intent = new Intent(getActivity(), LockIndexActivity.class);
        intent.putExtra("device", deviceInfo);
        startActivity(intent);
    }

    private void nav2AddDevice() {
        Intent intent = new Intent(getActivity(), AddDeviceActivity.class);
        startActivity(intent);
    }

}
