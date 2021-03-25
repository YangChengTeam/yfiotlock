package com.yc.yfiotlock.controller.fragments.lock.ble;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.SafePwdCreateActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.add.ScanDeviceActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.MyFamilyActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.model.engin.IndexEngin;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.SafeUtils;
import com.yc.yfiotlock.view.adapters.IndexDeviceAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;

import static android.app.Activity.RESULT_OK;

public class IndexFragment extends BaseFragment {


    @BindView(R.id.iv_device_add)
    View deviceAddBtn;

    @BindView(R.id.rv_devices)
    RecyclerView devicesRecyclerView;

    private IndexDeviceAdapter indexDeviceAdapter;
    private IndexEngin indexEngin;
    

    private FamilyInfo familyInfo;
    private DeviceInfo mDeviceInfo;

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
                nav2LockIndex();
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

    private void nav2LockIndex() {
        switch (SafeUtils.getSafePwdType(mDeviceInfo)) {
            case SafeUtils.NO_PASSWORD:
                real2LockIndex(mDeviceInfo);
                break;
            case SafeUtils.PASSWORD_TYPE:
                SafePwdCreateActivity.startCheck(getActivity());
                break;
            case SafeUtils.FINGERPRINT_TYPE:
                SafeUtils.useFinger(getActivity(), new Callback<String>() {
                    @Override
                    public void onSuccess(String resultInfo) {
                        real2LockIndex(mDeviceInfo);
                    }

                    @Override
                    public void onFailure(Response response) {

                    }
                });
                break;
            default:
                break;
        }

    }

    private void real2LockIndex(DeviceInfo deviceInfo) {
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
                    && SafeUtils.getSafePwd(mDeviceInfo).equals(data.getStringExtra("pwd"))) {
                real2LockIndex(mDeviceInfo);
            } else {
                ToastCompat.show(getContext(), "密码错误");
            }
        }
    }
}
