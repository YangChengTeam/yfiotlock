package com.yc.yfiotlock.controller.fragments.lock.ble;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.view.adapters.IndexDeviceAdapter;

import java.util.ArrayList;
import java.util.List;

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
    }

    private void setRv() {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        deviceInfos.add(new DeviceInfo());
        indexDeviceAdapter = new IndexDeviceAdapter(deviceInfos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        devicesRecyclerView.setLayoutManager(gridLayoutManager);
        devicesRecyclerView.setAdapter(indexDeviceAdapter);

        indexDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {

        });

    }

}
