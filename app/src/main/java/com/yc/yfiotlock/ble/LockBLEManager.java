package com.yc.yfiotlock.ble;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.callback.BleGattCallback;
import com.yc.yfiotlock.libs.fastble.callback.BleMtuChangedCallback;
import com.yc.yfiotlock.libs.fastble.callback.BleScanCallback;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fastble.exception.BleException;
import com.yc.yfiotlock.libs.fastble.scan.BleScanRuleConfig;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;
import com.yc.yfiotlock.utils.BleUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockBLEManager {
    public static final String DEVICE_NAME = "YF-L1";
    public static final int OP_INTERVAL_TIME = 250;
    public static final String PIN_CODE = "123456";
    public static byte GROUP_TYPE = 0;
    public static final byte GROUP_TYPE_TEMP_PWD = 2;
    public static final byte GROUP_ADMIN = 0;
    public static final byte GROUP_HIJACK = 3;
    public static final byte ALARM_TYPE = 2;
    public static final byte NORMAL_TYPE = 1;

    public static final int OP_TIMEOUT = 20000;
    public static final int OPEN_LOCK_FINGERPRINT = 1;
    public static final int OPEN_LOCK_PASSWORD = 2;
    public static final int OPEN_LOCK_CARD = 3;

    private LockBLEManager() {
    }

    private static final LockBLEManager instance = new LockBLEManager();

    public static LockBLEManager getInstance() {
        return instance;
    }

    private final Map<String, BleDevice> scannedBleDevices = new HashMap<>();

    public void initBle(Application context) {
        BleManager.getInstance()
                .setReConnectCount(1, 1000)
                .setSplitWriteNum(LockBLEPackage.getMtu())
                .setConnectOverTime(10000 * 5)
                .setOperateTimeout(10000).init(context);
        initBleState(context);
    }

    public void initConfig() {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setDeviceName(false, DEVICE_NAME)
                .setScanTimeOut(12000);
        BleManager.getInstance().initScanRule(builder.build());
    }


    public void initConfig2(String mac) {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setDeviceMac(mac)
                .setScanTimeOut(12000);
        BleManager.getInstance().initScanRule(builder.build());
    }

    public void clear() {
        BleManager.getInstance().disconnectAllDevice();
    }

    public boolean isConnected(BleDevice bleDevice) {
        return BleManager.getInstance().isConnected(bleDevice) && bleDevice.isMatch();
    }

    public void disConnect(BleDevice bleDevice) {
        BleManager.getInstance().disconnect(bleDevice);
    }

    public void destory() {
        BleManager.getInstance().destroy();
    }

    public void setMtu(BleDevice bleDevice) {
        // 设置mtu
        BleManager.getInstance().setMtu(bleDevice, LockBLEPackage.getMtu(), new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                LockBLESender.bleNotify(bleDevice);
            }

            @Override
            public void onMtuChanged(int mtu) {
                // 设置MTU成功，并获得当前设备传输支持的MTU值
                LockBLEPackage.setMtu(mtu);
                LockBLESender.bleNotify(bleDevice);
            }
        });
    }

    public interface LockBLEScanCallbck {
        void onScanStarted();

        void onScanning(BleDevice bleDevice);

        void onScanSuccess(List<BleDevice> scanResultList);

        void onScanFailed();
    }

    public static final int REQUEST_GPS = 4;

    public void stopScan() {
        BleManager.getInstance().cancelScan();
    }

    public void scan(BaseActivity activity, LockBLEScanCallbck callbck) {
        PermissionHelper mPermissionHelper = activity.getPermissionHelper();
        mPermissionHelper.checkAndRequestPermission(activity, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !BleUtil.checkGPSIsOpen(activity)) {
                    new GeneralDialog(activity)
                            .setTitle("提示")
                            .setMsg("为了更精确的扫描到Bluetooth LE设备, 请打开GPS定位")
                            .setPositiveText("确定")
                            .setOnPositiveClickListener(dialog -> {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivityForResult(intent, REQUEST_GPS);
                            })
                            .setNegativeText("取消").show();
                    return;
                }
                startScan(callbck);
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.show(activity, "授权失败, 无法扫描蓝牙设备");
            }
        });
    }

    // 蓝牙状态监听
    private void initBleState(Context context) {
        BleStateReceiver bleStateReceiver = new BleStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bleStateReceiver, intentFilter);
    }

    private static class BleStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            VUiKit.postDelayed(1000, () -> {
                                EventBus.getDefault().post(new ReScanEvent());
                            });
                            break;
                        case BluetoothAdapter.STATE_ON:
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            break;
                        case BluetoothAdapter.ERROR:
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

        }
    }


    // 开始扫描
    private void startScan(LockBLEScanCallbck callbck) {
        if (!BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();
            return;
        }
        // 设置搜索状态
        callbck.onScanStarted();

        // 开始搜索
        scannedBleDevices.forEach((key, value) -> {
            callbck.onScanning(value);
            Log.d("scan cache", "name:" + value.getName() + " mac:" + value.getMac());
        });

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (bleDevice == null || TextUtils.isEmpty(bleDevice.getName())) return;
                if(!bleDevice.getName().equals(DEVICE_NAME)) return;
                Log.d("scan ble", "name:" + bleDevice.getName() + " mac:" + bleDevice.getMac());

                if (scannedBleDevices.get(bleDevice.getMac()) == null) {
                    scannedBleDevices.put(bleDevice.getMac(), bleDevice);
                    callbck.onScanning(bleDevice);
                } else {
                    scannedBleDevices.put(bleDevice.getMac(), bleDevice);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (scanResultList.size() == 0 && scannedBleDevices.size() == 0) {
                    // 搜索完成未发现设备
                    callbck.onScanFailed();
                } else {
                    callbck.onScanSuccess(scanResultList);
                }
            }
        });
    }

    public interface LockBLEConnectCallbck {
        void onConnectStarted();

        void onDisconnect(BleDevice bleDevice);

        void onConnectSuccess(BleDevice bleDevice);

        void onConnectFailed();
    }


    public void connect(BleDevice bleDevice, LockBLEConnectCallbck callbck) {
        callbck.onConnectStarted();
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                callbck.onConnectFailed();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                callbck.onConnectSuccess(bleDevice);
                setMtu(bleDevice);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                BleManager.getInstance().disconnect(bleDevice);
                // 设置连接失败状态
                callbck.onDisconnect(bleDevice);
            }
        });
    }
}
