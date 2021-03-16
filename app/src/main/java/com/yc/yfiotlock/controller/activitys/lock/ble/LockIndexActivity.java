package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLEUtils;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.LockLogActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.helper.ShakeSensor;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.model.bean.OpenLockReConnectEvent;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.AnimatinUtils;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class LockIndexActivity extends BaseActivity {
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

    private LockEngine lockEngine;
    private DeviceInfo lockInfo;

    public DeviceInfo getLockInfo() {
        return lockInfo;
    }

    private BleDevice bleDevice;

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    private LockBLESend lockBleSend;

    private CONNECT_STATUS connectStatus;

    enum CONNECT_STATUS {
        CONNECTING,
        CONNECT_FAILED,
        CONNECT_SUCC,
        CONNECT_OPING
    }

    private static LockIndexActivity mInstance;

    public static LockIndexActivity getInstance() {
        return mInstance;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_index;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        lockEngine = new LockEngine(this);
    }

    @Override
    protected void initViews() {
        mInstance = this;
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
            if (connectStatus == CONNECT_STATUS.CONNECT_FAILED) {
                scan();
            }
        });

        RxView.longClicks(tabView).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (connectStatus == CONNECT_STATUS.CONNECT_SUCC) {
                open();
            }
        });
        scan();

        shakeSensor = new ShakeSensor(this);
        shakeSensor.setShakeListener(new ShakeSensor.OnShakeListener() {
            @Override
            public void onShakeComplete(SensorEvent event) {
                if (connectStatus == CONNECT_STATUS.CONNECT_SUCC) {
                    // 开门
                    open();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        shakeSensor.register();

        // 重新连接
        if (bleDevice != null && connectStatus == CONNECT_STATUS.CONNECT_FAILED) {
            reconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        shakeSensor.unregister();
    }

    private void stopAnimations() {
        loadingIv.clearAnimation();
        tabView.clearAnimation();
        tabView2.clearAnimation();
    }

    private void startAnimations() {
        AnimatinUtils.rotate(loadingIv);
        AnimatinUtils.scale(tabView, 0.05f);
        AnimatinUtils.scale(tabView2, 0.05f);
    }

    private void open() {
        connectStatus = CONNECT_STATUS.CONNECT_OPING;

        loadingIv.setImageResource(R.mipmap.three);
        statusTitleTv.setText("正在开锁...");
        startAnimations();

        if (lockBleSend != null) {
            lockBleSend.send((byte) 0x02, (byte) 0x01, LockBLEOpCmd.open(this));
        }
    }

    public boolean isConnected() {
        return connectStatus == CONNECT_STATUS.CONNECT_SUCC || connectStatus == CONNECT_STATUS.CONNECT_OPING;
    }

    private static final int REQUEST_GPS = 4;

    private void scan() {
        mPermissionHelper.checkAndRequestPermission(LockIndexActivity.this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !LockBLEUtils.checkGPSIsOpen(LockIndexActivity.this)) {
                    new AlertDialog.Builder(LockIndexActivity.this)
                            .setTitle("提示")
                            .setMessage("为了更精确的扫描到Bluetooth LE设备, 请打开GPS定位")
                            .setPositiveButton("确定", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, REQUEST_GPS);
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                    return;
                }
                startScan();
            }

            @Override
            public void onRequestPermissionError() {
                Toast.makeText(LockIndexActivity.this, "授权失败, 无法扫描蓝牙设备", Toast.LENGTH_LONG).show();
            }
        });
    }

    // 开始扫描
    private void startScan() {
        if (!BleManager.getInstance().isBlueEnable()) {
            Toast.makeText(LockIndexActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
            BleManager.getInstance().enableBluetooth();
            return;
        }

        // 设置搜索状态
        connectStatus = CONNECT_STATUS.CONNECTING;
        loadingIv.setImageResource(R.mipmap.two);
        statusIv.setImageResource(R.mipmap.icon_bluetooth);
        statusTitleTv.setText("搜索门锁中...");
        opDespTv.setText("请打开手机蓝牙贴近门锁");
        startAnimations();

        // 开始搜索
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                // 搜索到后开始连接
                connect(bleDevice);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (scanResultList.size() == 0) {
                    // 搜索完成未发现设备
                    connectStatus = CONNECT_STATUS.CONNECT_FAILED;
                    stopAnimations();
                    statusTitleTv.setText("未搜索到门锁");
                    opDespTv.setText("请打开手机蓝牙贴近门锁");
                    statusIv.setImageResource(R.mipmap.icon_nolink);
                    loadingIv.setImageResource(R.mipmap.one);
                }
            }
        });
    }

    // 连接蓝牙
    private void connect(final BleDevice bleDevice) {
        statusTitleTv.setText("连接门锁中...");
        opDespTv.setText("请打开手机蓝牙贴近门锁");
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                statusTitleTv.setText("连接门锁中...");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                connectStatus = CONNECT_STATUS.CONNECT_FAILED;
                stopAnimations();
                statusTitleTv.setText("门锁连接失败");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                statusIv.setImageResource(R.mipmap.icon_nolink);
                loadingIv.setImageResource(R.mipmap.one);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LockIndexActivity.this.bleDevice = bleDevice;
                lockBleSend = new LockBLESend(LockIndexActivity.this, bleDevice);

                // 设置连接成功状态
                connectStatus = CONNECT_STATUS.CONNECT_SUCC;
                stopAnimations();
                statusTitleTv.setText("已连接门锁");
                opDespTv.setText("长按按钮或者摇一摇开锁");
                loadingIv.setImageResource(R.mipmap.one);
                statusIv.setImageResource(R.mipmap.icon_lock_close);

                LockBLEManager.setMtu(bleDevice);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 设置连接失败状态
                connectStatus = CONNECT_STATUS.CONNECT_FAILED;
                stopAnimations();
                statusTitleTv.setText("门锁未连接");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                statusIv.setImageResource(R.mipmap.icon_nolink);
                loadingIv.setImageResource(R.mipmap.one);
            }
        });
    }

    // 重新逻辑
    private void reconnect() {
        GeneralDialog generalDialog = new GeneralDialog(this);
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("蓝牙连接已断开，请将手机靠近 门锁后重试");
        generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                connect(bleDevice);
            }
        });
        generalDialog.show();
    }

    // 处理响应
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProcess(LockBLEData bleData) {
        if (bleData != null && bleData.getMcmd() == (byte) 0x02 && bleData.getScmd() == (byte) 0x01) {
            connectStatus = CONNECT_STATUS.CONNECT_SUCC;
            stopAnimations();
            if (bleData.getStatus() == (byte) 0x00) {
                statusTitleTv.setText("门锁已打开");
            } else {
                statusTitleTv.setText("门锁已连接");
                loadingIv.setImageResource(R.mipmap.one);
            }
        }
    }

    // 进入开门方式管理
    private void nav2OpenLock() {
        Intent intent = new Intent(this, BaseOpenLockManagerActivity.class);
        startActivity(intent);
    }

    // 进入访客管理
    private void nav2Vm() {
        Intent intent = new Intent(this, VisitorManageActivity.class);
        startActivity(intent);
    }

    // 进入日志管理
    private void nav2Log() {
        Intent intent = new Intent(this, LockLogActivity.class);
        startActivity(intent);
    }

    // 进入设置
    private void nav2setting() {
        Intent intent = new Intent(this, LockSettingActivity.class);
        startActivity(intent);
    }

    private int setCountInfo(){
        int type = 1;
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL+ type, OpenLockCountInfo.class);
        if (countInfo != null) {
            openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
        }
        return type;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        setCountInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReConnect(OpenLockReConnectEvent object) {
        connect(bleDevice);
    }

    // 开门方式数量
    private void loadLockOpenCountInfo() {
        int type = setCountInfo();
        lockEngine.getOpenLockInfoCount(lockInfo.getId(), type + "").subscribe(new Subscriber<ResultInfo<OpenLockCountInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<OpenLockCountInfo> openLockCountInfoResultInfo) {
                if (openLockCountInfoResultInfo.getCode() == 1 && openLockCountInfoResultInfo.getData() != null) {
                    OpenLockCountInfo countInfo = openLockCountInfoResultInfo.getData();
                    openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
                    CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // GPS授权回调
        if (requestCode == REQUEST_GPS) {
            if (LockBLEUtils.checkGPSIsOpen(this)) {
                scan();
            }
        }
        // 处理授权回调
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }
}
