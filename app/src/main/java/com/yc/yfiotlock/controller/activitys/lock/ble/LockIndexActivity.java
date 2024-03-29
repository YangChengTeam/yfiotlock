package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEBaseCmd;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.LockLogActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.helper.CloudHelper;
import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.sensor.ShakeSensor;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.model.bean.eventbus.ForamtErrorEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockCountRefreshEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockReConnectEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.bean.lock.TimeInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.BleUtil;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

public class LockIndexActivity extends BaseActivity implements LockBLESender.NotifyCallback {
    @BindView(R.id.iv_back)
    View backBtn;
    @BindView(R.id.iv_setting)
    View settingBtn;

    @BindView(R.id.cd_open_lock)
    View openLockBtn;
    @BindView(R.id.cd_log)
    View logBtn;

    @BindView(R.id.iv_loading)
    ImageView loadingIv;
    @BindView(R.id.iv_tap)
    View tabView;
    @BindView(R.id.iv_tap2)
    View tabView2;
    @BindView(R.id.iv_status)
    ImageView statusIv;
    @BindView(R.id.tv_status_title)
    TextView statusTitleTv;
    @BindView(R.id.tv_op_desp)
    TextView opDespTv;

    @BindView(R.id.tv_open_count)
    TextView openCountTv;
    @BindView(R.id.cd_vm)
    CardView mCdVm;

    private ShakeSensor shakeSensor;
    private BleDevice bleDevice;
    private LockEngine lockEngine;
    private DeviceEngin deviceEngin;
    private FamilyInfo familyInfo;
    private DeviceInfo lockInfo;
    private LockBLESender lockBleSender;
    private LockBLESender syncTimeBlesender;

    private CloudHelper cloudHelper;

    private boolean isOpening;

    public DeviceInfo getLockInfo() {
        return lockInfo;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public FamilyInfo getFamilyInfo() {
        return familyInfo;
    }

    public LockBLESender getLockBleSender() {
        return lockBleSender;
    }

    private static WeakReference<LockIndexActivity> mInstance;


    public static LockIndexActivity getInstance() {
        if (mInstance != null && mInstance.get() != null) {
            return mInstance.get();
        }
        System.exit(1);
        return null;
    }

    public static void safeFinish() {
        if (getInstance() != null) {
            getInstance().finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_index;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        familyInfo = (FamilyInfo) getIntent().getSerializableExtra("family");
        bleDevice = getIntent().getParcelableExtra("bleDevice");
        deviceEngin = new DeviceEngin(this);
        lockEngine = new LockEngine(this);
        cloudHelper = new CloudHelper(this);
        syncTimeBlesender = new LockBLESender(this, bleDevice, lockInfo.getKey());
    }

    /**
     * 设置用户的UI 而非管理员
     */
    private void setShareDeviceUi() {
        mCdVm.setVisibility(View.GONE);
    }

    @Override
    protected void initViews() {
        mInstance = new WeakReference<>(this);
        setFullScreen();
        loadLockOpenCountInfo();
        if (bleDevice == null) {
            scan();
        } else {
            if (LockBLEManager.getInstance().isConnected(bleDevice)) {
                MMKV.defaultMMKV().getBoolean("ismatch" + lockInfo.getMacAddress(), true);
                initSends();
                setConnectedInfo();
                bleSynctime(true);
            }
        }

        reConnectDialog = new GeneralDialog(this);
        reConnectDialog.setTitle("温馨提示");
        reConnectDialog.setMsg("蓝牙连接已断开，请将手机靠近 门锁后重试");
        reConnectDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                scan();
            }
        });

        if (lockInfo.isShare()) {
            setShareDeviceUi();
        }
    }

    private void initShakeSensor() {
        shakeSensor = new ShakeSensor(this);
        shakeSensor.register();
        shakeSensor.setShakeListener(new ShakeSensor.OnShakeListener() {
            @Override
            public void onShakeComplete(SensorEvent event) {
                if (LockBLEManager.getInstance().isConnected(bleDevice) && !isBleWorking()) {
                    // 开门
                    bleOpen();
                    vibrate();
                }
            }
        });
    }

    private void destoryShakeSensor() {
        shakeSensor.unregister();
        shakeSensor = null;
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        RxView.clicks(backBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            finish();
        });

        RxView.clicks(settingBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2setting();
        });

        RxView.clicks(openLockBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2OpenLock();
        });

        RxView.clicks(logBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2Log();
        });

        RxView.clicks(mCdVm).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2Vm();
        });

        RxView.clicks(tabView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (LockBLEManager.getInstance().isConnected(bleDevice)) {
                return;
            }

            if (isBleWorking()) {
                return;
            }

            if (isBleOffline()) {
                return;
            }

            if (bleDevice == null) {
                scan();
            } else {
                connect(bleDevice);
            }
        });

        RxView.longClicks(tabView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (LockBLEManager.getInstance().isConnected(bleDevice) && !isBleWorking()) {
                bleOpen();
            }
        });
    }

    public boolean isBleWorking() {
        return "搜索门锁中...".equals(statusTitleTv.getText().toString()) || "连接门锁中...".equals(statusTitleTv.getText().toString());
    }

    public boolean isBleOffline() {
        return "门锁已离线".equals(statusTitleTv.getText().toString());
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{200, 500}, -1);
        }
    }

    private void initSends() {
        if (lockBleSender == null) {
            lockBleSender = new LockBLESender(this, bleDevice, lockInfo.getKey());
            lockBleSender.registerNotify();
            lockBleSender.setNotifyCallback(this);
        }
    }

    private void registerNotify() {
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(this);
            lockBleSender.registerNotify();
        }

        if (syncTimeBlesender != null) {
            syncTimeBlesender.registerNotify();
            syncTimeBlesender.setNotifyCallback(this);
        }
    }

    private void unregisterNotify() {
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(null);
            lockBleSender.unregisterNotify();
        }

        if (syncTimeBlesender != null) {
            syncTimeBlesender.setNotifyCallback(null);
            syncTimeBlesender.unregisterNotify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cloudHelper.registerNotify();

        initShakeSensor();
        registerNotify();

        // 重新连接
        if (bleDevice != null && !LockBLEManager.getInstance().isConnected(bleDevice)) {
            reconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterNotify();
        destoryShakeSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lockBleSender != null) {
            lockBleSender.clear();
        }

        if (lockEngine != null) {
            lockEngine.cancelAll();
        }

        if (cloudHelper != null) {
            cloudHelper.unregisterNotify();
        }

        BleManager.getInstance().destroy();

        stopAnimations();

        LogUtil.msg("已清理");
    }

    private void stopAnimations() {
        loadingIv.clearAnimation();
        tabView.clearAnimation();
        tabView2.clearAnimation();
    }

    private void startAnimations() {
        AnimatinUtil.rotate(loadingIv);
        AnimatinUtil.scale(tabView, 0.05f);
        AnimatinUtil.scale(tabView2, 0.05f);
    }


    private void bleOpen() {
        if (isOpening) {
            return;
        }

        isOpening = true;

        loadingIv.setImageResource(R.mipmap.three);
        statusTitleTv.setText("正在开锁...");
        startAnimations();

        if (lockBleSender != null) {
            lockBleSender.send(LockBLEOpCmd.MCMD, LockBLEOpCmd.SCMD_OPEN, LockBLEOpCmd.open(lockInfo.getKey()));
        }
    }

    private void bleCheckLock() {
        if (lockBleSender != null) {
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_CHECK_LOCK, LockBLESettingCmd.checkLock(lockInfo.getOrigenKey(), lockInfo.getKey()));
        }
    }

    private void scan() {
        LockBLEManager.getInstance().initConfig2(lockInfo.getMacAddress());
        LockBLEManager.getInstance().scan(this, new LockBLEManager.LockBLEScanCallbck() {
            @Override
            public void onScanStarted() {
                // 设置搜索状态
                loadingIv.setImageResource(R.mipmap.two);
                statusIv.setImageResource(R.mipmap.icon_bluetooth);
                statusTitleTv.setText("搜索门锁中...");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                startAnimations();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                // 搜索到后开始连接
                if (!bleDevice.getMac().equals(lockInfo.getMacAddress())) {
                    return;
                }
                connect(bleDevice);
                LockBLEManager.getInstance().stopScan();
            }

            @Override
            public void onScanSuccess(List<BleDevice> scanResultList) {

            }

            @Override
            public void onScanFailed() {
                // 搜索完成未发现设备
                stopAnimations();
                statusTitleTv.setText("未搜索到门锁");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                statusIv.setImageResource(R.mipmap.icon_nolink);
                loadingIv.setImageResource(R.mipmap.one);
            }
        });
    }

    // 已链接状态
    private void setConnectedInfo() {
        stopAnimations();
        statusTitleTv.setText("已连接门锁");
        opDespTv.setText("长按按钮或者摇一摇开锁");
        loadingIv.setImageResource(R.mipmap.one);
        statusIv.setImageResource(R.mipmap.icon_lock_close);
    }

    // 正在链接状态
    private void setConnectingInfo() {
        startAnimations();
        statusTitleTv.setText("连接门锁中...");
        opDespTv.setText("请打开手机蓝牙贴近门锁");
        loadingIv.setImageResource(R.mipmap.two);
        statusIv.setImageResource(R.mipmap.icon_bluetooth);
    }

    // 连接失败
    private void setConnectFailureInfo() {
        statusTitleTv.setText("门锁未连接");
        opDespTv.setText("请打开手机蓝牙贴近门锁");
        statusIv.setImageResource(R.mipmap.icon_nolink);
        loadingIv.setImageResource(R.mipmap.one);
    }

    // 设置已离线
    private void setOffsetlineInfo() {
        statusTitleTv.setText("门锁已离线");
        opDespTv.setText("");
        statusIv.setImageResource(R.mipmap.icon_nolink);
        loadingIv.setImageResource(R.mipmap.one);
    }


    // 连接蓝牙
    private void connect(final BleDevice bleDevice) {
        isOpening = false;
        LockBLEManager.getInstance().connect(bleDevice, new LockBLEManager.LockBLEConnectCallbck() {
            @Override
            public void onConnectStarted() {
                setConnectingInfo();
            }

            @Override
            public void onDisconnect(BleDevice bleDevice) {
                // 设置连接失败状态
                stopAnimations();
                setConnectFailureInfo();
                if (lockBleSender != null) {
                    lockBleSender.clear();
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice) {
                LockIndexActivity.this.bleDevice = bleDevice;
                initSends();
            }

            @Override
            public void onConnectFailed() {
                stopAnimations();
                setConnectFailureInfo();
            }
        });
    }

    // 同步时间
    protected void bleSynctime(boolean wakup) {
        if (syncTimeBlesender != null) {
            deviceEngin.getTime().subscribe(new Action1<ResultInfo<TimeInfo>>() {
                @Override
                public void call(ResultInfo<TimeInfo> info) {
                    if (info != null && info.getCode() == 1 && info.getData() != null) {
                        byte[] cmdBytes = LockBLESettingCmd.syncTime(lockInfo.getKey(), info.getData().getTime());
                        syncTimeBlesender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_SYNC_TIME, cmdBytes, wakup);
                    }
                }
            });
        }
    }


    // 重连逻辑
    private GeneralDialog reConnectDialog;

    private void reconnect() {
        reConnectDialog.show();
    }

    // 进入开门方式管理
    private void nav2OpenLock() {
        Intent intent = new Intent(this, OpenLockManagerActivity.class);
        startActivity(intent);
    }

    // 进入访客管理
    private void nav2Vm() {
        Intent intent = new Intent(getContext(), VisitorManageActivity.class);
        startActivity(intent);
    }

    // 进入日志管理
    private void nav2Log() {
        Intent intent = new Intent(getContext(), LockLogActivity.class);
        startActivity(intent);
    }

    // 进入设置
    private void nav2setting() {
        Intent intent = new Intent(this, LockSettingActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void setCountInfo() {
        int groupType = 1;
        String key = "locker_count_" + lockInfo.getId() + groupType;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
        } else {
            countInfo = new OpenLockCountInfo();
            CacheUtil.setCache(key, countInfo);
            lockEngine.getOpenLockInfoCount(lockInfo.getId() + "", groupType + "").subscribe(new Action1<ResultInfo<OpenLockCountInfo>>() {
                @Override
                public void call(ResultInfo<OpenLockCountInfo> openLockCountInfoResultInfo) {
                    if (openLockCountInfoResultInfo.getCode() == 1 && openLockCountInfoResultInfo.getData() != null) {
                        OpenLockCountInfo countInfo = openLockCountInfoResultInfo.getData();
                        openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
                        CacheUtil.setCache(key, countInfo);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        setCountInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockCountRefreshEvent object) {
        setCountInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReConnect(OpenLockReConnectEvent object) {
        connect(bleDevice);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReScan(ReScanEvent object) {
        scan();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDeviceChange(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFormatError(ForamtErrorEvent object) {
        bleDevice.setMatch(false);
        setOffsetlineInfo();
        MMKV.defaultMMKV().putBoolean("ismatch" + lockInfo.getMacAddress(), false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotify(BleNotifyEvent bleNotifyEvent) {
        if (bleNotifyEvent.getStatus() == BleNotifyEvent.onNotifySuccess) {
            bleCheckLock();
        }
    }

    // 开门方式数量
    private void loadLockOpenCountInfo() {
        setCountInfo();
        int groupType = 1;
        String key = "locker_count_" + lockInfo.getId() + groupType;
        lockEngine.getOpenLockInfoCount(lockInfo.getId() + "", groupType + "").subscribe(new Action1<ResultInfo<OpenLockCountInfo>>() {
            @Override
            public void call(ResultInfo<OpenLockCountInfo> openLockCountInfoResultInfo) {
                if (openLockCountInfoResultInfo.getCode() == 1 && openLockCountInfoResultInfo.getData() != null) {
                    OpenLockCountInfo countInfo = openLockCountInfoResultInfo.getData();
                    openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
                    CacheUtil.setCache(key, countInfo);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // GPS授权回调
        if (requestCode == LockBLEManager.REQUEST_GPS) {
            if (BleUtil.checkGPSIsOpen(this)) {
                scan();
            }
        }
        // 处理授权回调
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }


    private int retryCount = 3;

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEOpCmd.MCMD && lockBLEData.getScmd() == LockBLEOpCmd.SCMD_OPEN) {
            stopAnimations();
            statusIv.setImageResource(R.mipmap.icon_lock_open);
            statusTitleTv.setText("门锁已打开");
            // 恢复状态  我们目前版本使用机械反锁，所以门开关的状态事件目前版本是没有的。
            VUiKit.postDelayed(5000, () -> {
                isOpening = false;
                if (LockBLEManager.getInstance().isConnected(bleDevice)) {
                    setConnectedInfo();
                    if (shakeSensor != null) {
                        shakeSensor.setCallback(false);
                    }
                }
            });
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_SYNC_TIME) {
            LogUtil.msg("同步时间成功");
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHECK_LOCK) {
            LogUtil.msg("key匹配成功" + lockInfo.getKey());
            // 设置连接成功状态
            setConnectedInfo();
            bleDevice.setMatch(true);
            bleSynctime(false);
            MMKV.defaultMMKV().putBoolean("ismatch" + lockInfo.getMacAddress(), true);
            EventBus.getDefault().post(bleDevice);
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEOpCmd.MCMD && lockBLEData.getScmd() == LockBLEOpCmd.SCMD_OPEN) {
            isOpening = false;
            if (shakeSensor != null) {
                shakeSensor.setCallback(false);
            }
            if (LockBLEManager.getInstance().isConnected(bleDevice)) {
                setConnectedInfo();
            }
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_SYNC_TIME) {
            LogUtil.msg("同步时间失败");
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CHECK_LOCK) {
            if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_ERROR) {
                LogUtil.msg("key匹配失败");
                if (retryCount-- > 0) {
                    bleCheckLock();
                } else {
                    bleDevice.setMatch(false);
                    setOffsetlineInfo();
                    retryCount = 3;
                    MMKV.defaultMMKV().putBoolean("ismatch" + lockInfo.getMacAddress(), false);
                }
            } else {
                VUiKit.postDelayed(300, () -> {
                    bleCheckLock();
                });
            }
        }
    }

}
