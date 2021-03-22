package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtils;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.LockLogActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.sensor.ShakeSensor;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

public class LockIndexActivity extends BaseActivity implements LockBLESend.NotifyCallback {
    @BindView(R.id.iv_back)
    View backBtn;
    @BindView(R.id.iv_setting)
    View settingBtn;

    @BindView(R.id.cd_open_lock)
    View openLockBtn;
    @BindView(R.id.cd_log)
    View logBtn;
    @BindView(R.id.cd_vm)
    View vmBtn;

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

    private ShakeSensor shakeSensor;
    private BleDevice bleDevice;
    private LockEngine lockEngine;
    private FamilyInfo familyInfo;
    private DeviceInfo lockInfo;
    private LockBLESend lockBleSend;

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


    public LockBLESend getLockBleSend() {
        return lockBleSend;
    }

    private static WeakReference<LockIndexActivity> mInstance;

    public static LockIndexActivity getInstance() {
        if (mInstance != null && mInstance.get() != null) {
            return mInstance.get();
        }
        return null;
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
        lockEngine = new LockEngine(this);
    }

    @Override
    protected void initViews() {
        mInstance = new WeakReference<>(this);
        setFullScreen();

        loadLockOpenCountInfo();
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

        RxView.clicks(vmBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2Vm();
        });

        RxView.clicks(tabView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (LockBLEManager.isConnected(bleDevice)) return;
            if (statusTitleTv.getText().equals("连接门锁中...")) return;

            if (bleDevice == null) {
                scan();
            } else {
                connect(bleDevice);
            }
        });

        RxView.longClicks(tabView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (LockBLEManager.isConnected(bleDevice)) {
                open();
            }
        });

        if (bleDevice == null) {
            scan();
        } else {
            if (LockBLEManager.isConnected(bleDevice)) {
                lockBleSend = new LockBLESend(this, bleDevice);
                lockBleSend.registerNotify();
                lockBleSend.setNotifyCallback(this);
                setConnectedInfo();
            }
        }


        shakeSensor = new ShakeSensor(this);
        shakeSensor.setShakeListener(new ShakeSensor.OnShakeListener() {
            @Override
            public void onShakeComplete(SensorEvent event) {
                if (LockBLEManager.isConnected(bleDevice)) {
                    // 开门
                    open();
                    vibrate();
                }
            }
        });

        generalDialog = new GeneralDialog(this);
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("蓝牙连接已断开，请将手机靠近 门锁后重试");
        generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                connect(bleDevice);
            }
        });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{200, 500}, -1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shakeSensor.register();

        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(this);
            lockBleSend.registerNotify();
        }
        // 重新连接
        if (bleDevice != null && !LockBLEManager.isConnected(bleDevice)) {
            reconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
        }
        shakeSensor.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockBleSend != null) {
            lockBleSend.clear();
        }
        LockBLEManager.clear();
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

    private void open() {
        if (isOpening) {
            return;
        }
        isOpening = true;

        loadingIv.setImageResource(R.mipmap.three);
        statusTitleTv.setText("正在开锁...");
        startAnimations();

        if (lockBleSend != null) {
            lockBleSend.send((byte) 0x02, (byte) 0x01, LockBLEOpCmd.open(this));
        }

        VUiKit.postDelayed(8000, () -> {
            if (isOpening) {
                isOpening = false;
                if (LockBLEManager.isConnected(bleDevice)) {
                    setConnectedInfo();
                }
                ToastCompat.show(getContext(), "操作超时");
            }
        });
    }


    private void scan() {
        if (!BleManager.getInstance().isBlueEnable()) {
            ToastCompat.show(LockIndexActivity.this, "请先打开蓝牙");
            BleManager.getInstance().enableBluetooth();
            return;
        }
        LockBLEManager.initConfig2(lockInfo.getMacAddress());
        LockBLEManager.scan(this, new LockBLEManager.LockBLEScanCallbck() {
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

    // 连接蓝牙
    private void connect(final BleDevice bleDevice) {
        isOpening = false;
        LockBLEManager.connect(bleDevice, new LockBLEManager.LockBLEConnectCallbck() {
            @Override
            public void onConnectStarted() {
                setConnectingInfo();
            }

            @Override
            public void onDisconnect(BleDevice bleDevice) {
                // 设置连接失败状态
                stopAnimations();
                statusTitleTv.setText("门锁未连接");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                statusIv.setImageResource(R.mipmap.icon_nolink);
                loadingIv.setImageResource(R.mipmap.one);

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice) {
                LockIndexActivity.this.bleDevice = bleDevice;
                if (lockBleSend == null) {
                    lockBleSend = new LockBLESend(LockIndexActivity.this, bleDevice);
                    lockBleSend.registerNotify();
                    lockBleSend.setNotifyCallback(LockIndexActivity.this);
                    VUiKit.postDelayed(1000, () -> {
                        LockBLESend.bleNotify(bleDevice);
                    });
                }
                // 设置连接成功状态
                setConnectedInfo();

                LockBLEManager.setMtu(bleDevice);
            }

            @Override
            public void onConnectFailed() {
                stopAnimations();
                statusTitleTv.setText("门锁连接失败");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                statusIv.setImageResource(R.mipmap.icon_nolink);
                loadingIv.setImageResource(R.mipmap.one);
            }
        });
    }

    // 重新逻辑
    private GeneralDialog generalDialog;

    private void reconnect() {
        if (generalDialog.isShowing()) {
            generalDialog.show();
        }
    }

    // 进入开门方式管理
    private void nav2OpenLock() {
        Intent intent = new Intent(this, OpenLockManagerActivity.class);
        startActivity(intent);
    }

    // 进入访客管理
    private void nav2Vm() {
        VisitorManageActivity.start(this, lockInfo);
    }

    // 进入日志管理
    private void nav2Log() {
        LockLogActivity.start(this, lockInfo);
    }

    // 进入设置
    private void nav2setting() {
        Intent intent = new Intent(this, LockSettingActivity.class);
        intent.putExtra("device", lockInfo);
        startActivity(intent);
    }

    private int setCountInfo() {
        int type = 1;
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
        }
        return type;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        setCountInfo();
    }

    // 开门方式数量
    private void loadLockOpenCountInfo() {
        int type = setCountInfo();
        lockEngine.getOpenLockInfoCount(lockInfo.getId(), type + "").subscribe(new Action1<ResultInfo<OpenLockCountInfo>>() {
            @Override
            public void call(ResultInfo<OpenLockCountInfo> openLockCountInfoResultInfo) {
                if (openLockCountInfoResultInfo.getCode() == 1 && openLockCountInfoResultInfo.getData() != null) {
                    OpenLockCountInfo countInfo = openLockCountInfoResultInfo.getData();
                    openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
                    CacheUtil.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // GPS授权回调
        if (requestCode == LockBLEManager.REQUEST_GPS) {
            if (LockBLEUtils.checkGPSIsOpen(this)) {
                scan();
            }
        }
        // 处理授权回调
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }

    @Override
    public void onNotifyReady() {
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x02 && lockBLEData.getScmd() == (byte) 0x01) {
            stopAnimations();
            statusTitleTv.setText("门锁已打开");
            // 恢复状态  我们目前版本使用机械反锁，所以门开关的状态事件目前版本是没有的。
            VUiKit.postDelayed(5000, () -> {
                isOpening = false;
                if (LockBLEManager.isConnected(bleDevice)) {
                    setConnectedInfo();
                }
            });
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x02 && lockBLEData.getScmd() == (byte) 0x01) {
            setConnectedInfo();
            isOpening = false;
        }
    }
}
