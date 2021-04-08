package com.yc.yfiotlock.ble;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
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
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class LockBLEManager {
    public static final String DEVICE_NAME = "YF-L1";
    public static final int OP_INTERVAL_TIME = 200;
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

    public static void initBle(Application context) {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(5, 1000)
                .setSplitWriteNum(LockBLEPackage.getMtu())
                .setConnectOverTime(10000)
                .setOperateTimeout(5000).init(context);
    }

    public static void initConfig() {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setDeviceName(false, DEVICE_NAME)
                .setScanTimeOut(10000);
        BleManager.getInstance().initScanRule(builder.build());
    }

    public static void cancelScan() {
        BleManager.getInstance().cancelScan();
    }

    public static void initConfig2(String mac) {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setDeviceMac(mac)
                .setScanTimeOut(10000);
        BleManager.getInstance().initScanRule(builder.build());
    }

    public static void clear() {
        BleManager.getInstance().disconnectAllDevice();
    }

    public static boolean isConnected(BleDevice bleDevice) {
        return BleManager.getInstance().isConnected(bleDevice);
    }

    public static void destory(){
        BleManager.getInstance().destroy();
    }

    public static void setMtu(BleDevice bleDevice) {
        // 设置mtu
        BleManager.getInstance().setMtu(bleDevice, LockBLEPackage.getMtu(), new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                LockBLESend.bleNotify(bleDevice);
            }

            @Override
            public void onMtuChanged(int mtu) {
                // 设置MTU成功，并获得当前设备传输支持的MTU值
                LockBLEPackage.setMtu(mtu);
                LockBLESend.bleNotify(bleDevice);
            }
        });
    }

    public static final int REQUEST_GPS = 4;

    public interface LockBLEScanCallbck {
        void onScanStarted();

        void onScanning(BleDevice bleDevice);

        void onScanSuccess(List<BleDevice> scanResultList);

        void onScanFailed();
    }

    public static void scan(BaseActivity activity, LockBLEScanCallbck callbck) {
        PermissionHelper mPermissionHelper = activity.getPermissionHelper();
        mPermissionHelper.checkAndRequestPermission(activity, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !LockBLEUtils.checkGPSIsOpen(activity)) {

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
                startScan(activity, callbck);
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.show(activity, "授权失败, 无法扫描蓝牙设备");
            }
        });
    }

    // 开始扫描
    private static void startScan(Context context, LockBLEScanCallbck callbck) {
        if (!BleManager.getInstance().isBlueEnable()) {
            ToastCompat.show(context, "请先打开蓝牙", Toast.LENGTH_LONG);
            BleManager.getInstance().enableBluetooth();
            return;
        }
        // 设置搜索状态
        callbck.onScanStarted();
        // 开始搜索
        List<BleDevice> bleDevices = BleManager.getInstance().getAllConnectedDevice();
        if (bleDevices != null) {
            for (BleDevice bleDevice : bleDevices) {
                callbck.onScanning(bleDevice);
            }
        }
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                callbck.onScanning(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (bleDevice == null) return;
                callbck.onScanning(bleDevice);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (scanResultList.size() == 0) {
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

    public static void connect(BleDevice bleDevice, LockBLEConnectCallbck callbck) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                callbck.onConnectStarted();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                callbck.onConnectFailed();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                callbck.onConnectSuccess(bleDevice);
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
