package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLEPackage;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.LockLogActivity;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;
import com.yc.yfiotlock.demo.comm.ObserverManager;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.DeviceInfo;
import com.yc.yfiotlock.model.bean.EventStub;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.AnimatinUtils;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;

public class LockIndexActivity extends BaseActivity {

    public static final String SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String WRITE_CHARACTERISTIC_UUID = "49535343-8841-43f4-a8d4-ecbe34729bb3";
    public static final String NOTIFY_CHARACTERISTIC_UUID = "5833ff03-9b8b-5191-6142-22a4536ef123";

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


    private LockEngine lockEngine;
    private DeviceInfo lockInfo;

    private CONNECT_STATUS connectStatus;

    enum CONNECT_STATUS {
        CONNECTING,
        CONNECT_FAILED,
        CONNECT_SUCC
    }

    private BleDevice bleDevice;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_index;
    }

    @Override
    protected void initViews() {
        setFullScreen();

        initConfig();

        lockInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        lockEngine = new LockEngine(this);
        if (lockInfo != null) {
            loadLockOpenCountInfo();
        }

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
            } else if (connectStatus == CONNECT_STATUS.CONNECT_SUCC) {
                // 开门
                open();
            }
        });
        scan();
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

    private void initConfig() {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setServiceUuids(new UUID[]{UUID.fromString(SERVICE_UUID)})
                .setScanTimeOut(10000);
        BleManager.getInstance().initScanRule(builder.build());
    }

    private void open() {
        startAnimations();
        loadingIv.setImageResource(R.mipmap.three);

        byte[] bytes = LockBLEOpCmd.open(LockIndexActivity.this);
        op(bytes);
    }

    private void op(byte[] bytes) {
        BleManager.getInstance().write(
                bleDevice,
                SERVICE_UUID,
                WRITE_CHARACTERISTIC_UUID,
                bytes,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LockIndexActivity.this, "Write成功: " + HexUtil.formatHexString(justWrite, true).toUpperCase(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        Toast.makeText(LockIndexActivity.this, "Write失败: " + exception.getDescription(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private static final int REQUEST_GPS = 4;

    private void scan() {
        mPermissionHelper.checkAndRequestPermission(LockIndexActivity.this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !LockBLEUtil.checkGPSIsOpen(LockIndexActivity.this)) {
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
                statusTitleTv.setText("门锁未连接");
                opDespTv.setText("请打开手机蓝牙贴近门锁");
                statusIv.setImageResource(R.mipmap.icon_nolink);
                loadingIv.setImageResource(R.mipmap.one);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LockIndexActivity.this.bleDevice = bleDevice;

                // 蓝牙连接成功后开启监听
                bleNotify();

                // 设置连接成功状态
                connectStatus = CONNECT_STATUS.CONNECT_SUCC;
                stopAnimations();
                statusTitleTv.setText("已连接门锁");
                opDespTv.setText("长按按钮或者摇一摇开锁");
                loadingIv.setImageResource(R.mipmap.one);
                statusIv.setImageResource(R.mipmap.icon_lock_close);

                // 设置mtu
                BleManager.getInstance().setMtu(bleDevice, LockBLEPackage.getMtu(), new BleMtuChangedCallback() {
                    @Override
                    public void onSetMTUFailure(BleException exception) {
                        Toast.makeText(LockIndexActivity.this, "设置mtu失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMtuChanged(int mtu) {
                        // 设置MTU成功，并获得当前设备传输支持的MTU值
                        LockBLEPackage.setMtu(mtu);
                        Toast.makeText(LockIndexActivity.this, "设置mtu成功" + mtu, Toast.LENGTH_SHORT).show();
                    }
                });
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

                if (isActiveDisConnected) {
                    Toast.makeText(LockIndexActivity.this, bleDevice.getName() + getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LockIndexActivity.this, bleDevice.getName() + getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }
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

    // 蓝牙响应
    private void bleNotify() {
        BleManager.getInstance().notify(
                bleDevice,
                SERVICE_UUID,
                NOTIFY_CHARACTERISTIC_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Toast.makeText(LockIndexActivity.this, "Notify成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Toast.makeText(LockIndexActivity.this, "Notify失败:" + exception.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 解析响应
                        LockBLEData lockBLEData = LockBLEPackage.getData(data);
                        if (lockBLEData != null) {
                            processNotify(lockBLEData);
                        }
                        Toast.makeText(LockIndexActivity.this, "Notify响应:" + LockBLEUtil.toHexString(data), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // 处理响应
    private void processNotify(LockBLEData lockBLEData) {
        switch (lockBLEData.getMcmd()) {
            // 设置类
            case (byte) 0x01: {
                break;
            }
            // 操作类
            case (byte) 0x02: {
                switch (lockBLEData.getScmd()) {
                    case (byte) 0x01: {
                        if (lockBLEData.getStatus() == (byte) 0x00) {
                            stopAnimations();
                            loadingIv.setImageResource(R.mipmap.three);
                            statusIv.setImageResource(R.mipmap.icon_lock_open);
                        } else {

                        }
                        break;
                    }
                }
            }
        }
    }

    // 进入开门方式管理
    private void nav2OpenLock() {
        Intent intent = new Intent(this, OpenLockManagerActivity.class);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenLockCountInfo(OpenLockCountInfo countInfo) {
        if (countInfo != null) {
            openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }

    // 开门方式数量
    private void loadLockOpenCountInfo() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            openCountTv.setText("指纹:" + countInfo.getFingerprintCount() + "   密码:" + countInfo.getPasswordCount() + "   NFC:" + countInfo.getCardCount());
        }
        lockEngine.getOpenLockInfoCount(lockInfo.getId()).subscribe(new Subscriber<ResultInfo<OpenLockCountInfo>>() {
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
                    CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // GPS授权回调
        if (requestCode == REQUEST_GPS) {
            if (LockBLEUtil.checkGPSIsOpen(this)) {
                scan();
            }
        }
        // 处理授权回调
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }
}
