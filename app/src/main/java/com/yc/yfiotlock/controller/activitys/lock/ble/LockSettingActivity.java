package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.add.ConnectActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.download.DeviceDownloadManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
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
import rx.Subscriber;
import rx.functions.Action1;

public class LockSettingActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {
    @BindView(R.id.rv_setting)
    RecyclerView mRvSetting;

    private SettingAdapter mSettingAdapter;

    private LockBLESend lockBleSend;
    private DeviceInfo lockInfo;
    private DeviceEngin deviceEngin;
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
        lockBleSend = new LockBLESend(this, bleDevice, lockInfo.getKey());
        deviceEngin = new DeviceEngin(this);
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
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.changeVolume(lockInfo.getKey(), volume);
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_CHANGE_VOLUME, bytes, false);
        }
    }


    @Override
    protected void bindClick() {
        setClick(R.id.stv_del, () -> {
            GeneralDialog generalDialog = new GeneralDialog(getContext());
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("是否删除该设备");
            generalDialog.setOnPositiveClickListener(dialog -> {
                //是管理员的话就需要链接蓝牙 不是管理员是分享来的锁就可以直接删
                if (LockBLEManager.getInstance().isConnected(bleDevice) || lockInfo.isShare()) {
                    cloudDelDevice();
                } else {
                    ToastCompat.show(getContext(), "蓝牙未连接");
                }
            });
            generalDialog.show();
        });
    }

    private void cloudDelDevice() {
        mLoadingDialog.show("删除中...");
        deviceEngin.delDeviceVolume(lockInfo.getId() + "").subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    App.getApp().getMacList().remove(lockInfo.getMacAddress());
                    SafeUtil.setSafePwdType(lockInfo, 0);
                    EventBus.getDefault().post(new IndexRefreshEvent());
                    UserInfoCache.decDeviceNumber();
                    if (!lockInfo.isShare()) {
                        bleReset();
                    } else {
                        EventBus.getDefault().post(new IndexRefreshEvent());
                        LockIndexActivity.safeFinish();
                        finish();
                    }
                } else {
                    ToastCompat.show(getContext(), "删除失败");
                }
            }
        });
    }

    private void bleGetVersion() {
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.getVersion(lockInfo.getKey());
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_GET_VERSION, bytes, true);
        }
    }

    private void bleReset() {
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.reset(lockInfo.getKey());
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_RESET, bytes, true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(DeviceInfo deviceInfo) {
        loadData();
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
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(this);
            lockBleSend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
        }
    }


    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHANGE_VOLUME) {
            headView.setVolume(volume);
            lockInfo.setVolume(volume);
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_RESET) {
            finish();
            LockIndexActivity.safeFinish();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_GET_VERSION) {
            CacheUtil.setCache("firmwareVersion", new String(Arrays.copyOfRange(lockBLEData.getExtra(), 0, 6)));
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
