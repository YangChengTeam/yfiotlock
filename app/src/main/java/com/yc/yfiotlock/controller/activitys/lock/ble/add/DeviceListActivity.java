package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 扫描结果页
 *
 * @author Dullyoung
 */
public class DeviceListActivity extends BaseBackActivity {
    @BindView(R.id.tv_scan_title)
    TextView mTvScanTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;
    @BindView(R.id.stv_scan)
    SuperTextView mStvScan;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_add_activity_device_list;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRvDevices();
    }

    DeviceAdapter mDeviceAdapter;

    private void setRvDevices() {
        mDeviceAdapter = new DeviceAdapter(null);
        mRvDevices.setAdapter(mDeviceAdapter);
        mRvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtil.setItemDivider(getContext(), mRvDevices);
        mDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {

        });
    }

    @Override
    protected void bindClick() {
        setClick(mStvScan, () -> {
            ToastCompat.show(getContext(), "重新扫描");
            List<LockInfo> lockInfos = new ArrayList<>();
            lockInfos.add(new LockInfo("扬飞智能门锁YC-L1"));
            lockInfos.add(new LockInfo("扬飞智能门锁YC-L2"));
            lockInfos.add(new LockInfo("扬飞智能门锁YC-L3"));
            lockInfos.add(new LockInfo("扬飞智能门锁YC-L4"));
            lockInfos.add(new LockInfo("扬飞智能门锁YC-L5"));
            mDeviceAdapter.setNewInstance(lockInfos);
        });
    }


    private class DeviceAdapter extends BaseExtendAdapter<LockInfo> {

        public DeviceAdapter(@Nullable List<LockInfo> data) {
            super(R.layout.item_scan_device, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, LockInfo lockInfo) {
            holder.setText(R.id.tv_name, lockInfo.getName());
        }
    }
}
