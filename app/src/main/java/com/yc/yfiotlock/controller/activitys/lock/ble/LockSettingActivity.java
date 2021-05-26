package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.domain.ResultInfo;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.add.ConnectActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.download.DeviceDownloadManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceDelEvent;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.utils.SafeUtil;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.SettingSoundView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public class LockSettingActivity extends BaseBackActivity implements LockBLESender.NotifyCallback {
    @BindView(R.id.rv_setting)
    RecyclerView mRvSetting;

    private SettingAdapter mSettingAdapter;

    private LockBLESender lockBleSender;
    private DeviceInfo lockInfo;
    private DeviceEngin deviceEngin;
    private DeviceDao deviceDao;
    private BleDevice bleDevice;
    private SettingSoundView headView;
    private int volume;

    // 固件升级相关
    private UpdateInfo updateInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_setting;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
        bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBleSender = new LockBLESender(this, bleDevice, lockInfo.getKey());
        deviceEngin = new DeviceEngin(this);
        deviceDao = App.getApp().getDb().deviceDao();
    }

    @Override
    protected void initViews() {
        super.initViews();
        setRvSetting();
        loadData();

        if (LockBLEManager.getInstance().isConnected(bleDevice)) {
            bleGetVersion();
        } else {
            getUpdateInfo();
        }
    }

    private void getUpdateInfo() {
        updateInfo = DeviceDownloadManager.getInstance().getUpdateInfo();
        if (updateInfo != null && updateInfo.isUpgrade()) {
            mSettingAdapter.notifyDataSetChanged();
        }
        deviceEngin.getUpdateInfo(lockInfo.getFirmwareVersion()).subscribe(new Action1<ResultInfo<UpdateInfo>>() {
            @Override
            public void call(ResultInfo<UpdateInfo> info) {
                if (info != null && info.getCode() == 1) {
                    updateInfo = info.getData();
                    if (updateInfo != null && updateInfo.isUpgrade()) {
                        mSettingAdapter.notifyDataSetChanged();
                    }
                    DeviceDownloadManager.getInstance().setUpdateInfo(updateInfo);
                }
            }
        });
    }

    private void bleSetVolume(int volume) {
        if (lockBleSender != null) {
            byte[] bytes = LockBLESettingCmd.changeVolume(lockInfo.getKey(), volume);
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_CHANGE_VOLUME, bytes);
        }
    }


    @Override
    protected void bindClick() {
        setClick(R.id.stv_del, () -> {
            GeneralDialog generalDialog = new GeneralDialog(getContext());
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("是否删除该设备");
            generalDialog.setOnPositiveClickListener(dialog -> {
                // 是管理员的话就需要链接蓝牙 不是管理员是分享来的锁就可以直接删
                // 设备端重置了的也可以直接删
                boolean isMatch = MMKV.defaultMMKV().getBoolean("ismatch" + lockInfo.getMacAddress(), false);
                if (lockInfo.isShare() || !isMatch) {
                    localDeviceDel();
                } else {
                    if (LockBLEManager.getInstance().isConnected(bleDevice)) {
                        bleReset();
                    } else {
                        ToastCompat.show(getContext(), "蓝牙未连接");
                    }
                }
            });
            generalDialog.show();
        });
    }

    @SuppressLint("CheckResult")
    private void localDeviceDel() {
        deviceDao.deleteDeviceInfo(lockInfo.getMacAddress()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                mLoadingDialog.dismiss();
                SafeUtil.setSafePwdType(lockInfo, SafeUtil.NO_PASSWORD);
                UserInfoCache.decDeviceNumber();
                EventBus.getDefault().post(new CloudDeviceDelEvent(lockInfo));
                EventBus.getDefault().post(new IndexRefreshEvent());
                LockIndexActivity.safeFinish();
                if (lockBleSender != null) {
                    lockBleSender.clear();
                }
                finish();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    private void bleGetVersion() {
        if (lockBleSender != null) {
            byte[] bytes = LockBLESettingCmd.getVersion(lockInfo.getKey());
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_GET_VERSION, bytes);
        }
    }

    private void bleReset() {
        if (lockBleSender != null) {
            mLoadingDialog.show("删除设备中...");
            byte[] bytes = LockBLESettingCmd.reset(lockInfo.getKey());
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_RESET, bytes);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(DeviceInfo deviceInfo) {
        updateInfo = DeviceDownloadManager.getInstance().getUpdateInfo();
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnected(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    private void setRvSetting() {
        mSettingAdapter = new SettingAdapter(null);
        mRvSetting.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mRvSetting.setAdapter(mSettingAdapter);
        mSettingAdapter.setOnItemClickListener((adapter, view, position) -> {
            SettingInfo settingInfo = mSettingAdapter.getData().get(position);
            switch (settingInfo.getName()) {
                case "配置网络":
                    if (!LockBLEManager.getInstance().isConnected(bleDevice)) {
                        ToastCompat.show(getContext(), "蓝牙未连接");
                        return;
                    }
                    nav2Connect();
                    break;
                case "报警管理":
                    startActivity(new Intent(this, AlarmOpenLockManagerActivity.class));
                    break;
                case "设备信息":
                    Intent deviceInfoIntent = new Intent(this, DeviceInfoActivity.class);
                    startActivity(deviceInfoIntent);
                    break;
                case "设备名称":
                    Intent intent = new Intent(this, DeviceNameEditActivity.class);
                    startActivity(intent);
                    break;
                case "安全设置":
                    Intent safeIntent = new Intent(this, SafePwdSettingActivity.class);
                    startActivity(safeIntent);
                    break;
                case "帮助与反馈":
                    startActivity(new Intent(this, FAQActivity.class));
                    break;
                case "设备共享":
                    LockShareManageActivity.start(getContext(), lockInfo);
                    break;
                case "固件升级": {
                    Intent updateIntent = new Intent(this, FirmwareUpdateActivity.class);
                    if (updateInfo != null) {
                        updateIntent.putExtra("updateInfo", updateInfo);
                    }
                    startActivity(updateIntent);
                }

                break;
                default:
                    break;
            }
        });
        CommonUtil.setItemDivider(getContext(), mRvSetting);
        if (!lockInfo.isShare()) {
            headView = new SettingSoundView(this);
            headView.setDeviceMac(lockInfo.getMacAddress());
            headView.setVolume(headView.getVolume());
            headView.setOnSelectChangeListener(index -> {
                if (LockBLEManager.getInstance().isConnected(bleDevice)) {
                    volume = index;
                    bleSetVolume(volume);
                } else {
                    ToastCompat.show(getContext(), "蓝牙未连接");
                }
            });
            mSettingAdapter.setHeaderView(headView);
        }
    }

    private void nav2Connect() {
        Intent intent = new Intent(this, ConnectActivity.class);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", lockInfo);
        intent.putExtra("family", LockIndexActivity.getInstance().getFamilyInfo());
        intent.putExtra("isActiveDistributionNetwork", true);
        startActivity(intent);
    }


    private void loadData() {
        List<SettingInfo> settingInfos = new ArrayList<>();
        if (!lockInfo.isShare()) {
            settingInfos.add(new SettingInfo("配置网络", ""));
            settingInfos.add(new SettingInfo("报警管理", ""));
            settingInfos.add(new SettingInfo("设备信息", ""));
            settingInfos.add(new SettingInfo("设备名称", lockInfo.getName()));
            settingInfos.add(new SettingInfo("设备共享", ""));
            settingInfos.add(new SettingInfo("固件升级", ""));
        }
        settingInfos.add(new SettingInfo("安全设置", ""));
        settingInfos.add(new SettingInfo("帮助与反馈", ""));
        mSettingAdapter.setNewInstance(settingInfos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(this);
            lockBleSender.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(null);
            lockBleSender.unregisterNotify();
        }
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHANGE_VOLUME) {
            headView.setVolume(volume);
            lockInfo.setVolume(volume);
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_RESET) {
            mLoadingDialog.dismiss();
            localDeviceDel();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_VERSION) {
            lockInfo.setFirmwareVersion(new String(Arrays.copyOfRange(lockBLEData.getExtra(), 0, 6)));
            getUpdateInfo();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHANGE_VOLUME) {
            ToastCompat.show(getContext(), "设置失败");
            headView.resetVolume();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_VERSION) {
            getUpdateInfo();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_RESET) {
            mLoadingDialog.dismiss();
            ToastCompat.show(getContext(), "删除失败,请重试");
        }
    }

    private class SettingAdapter extends BaseExtendAdapter<SettingInfo> {
        public SettingAdapter(@Nullable List<SettingInfo> data) {
            super(R.layout.item_setting, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, SettingInfo settingInfo) {
            holder.setText(R.id.tv_name, settingInfo.getName());
            holder.setText(R.id.tv_value, settingInfo.getValue());
            if (settingInfo.getName().equals("固件升级") && updateInfo != null && updateInfo.isUpgrade()) {
                holder.setVisible(R.id.view_update, true);
            } else {
                holder.setVisible(R.id.view_update, false);
            }
        }
    }

    private class SettingInfo {
        private String name;
        private String value;

        public SettingInfo(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
