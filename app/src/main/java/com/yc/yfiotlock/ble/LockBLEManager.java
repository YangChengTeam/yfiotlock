package com.yc.yfiotlock.ble;

import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.demo.comm.ObserverManager;
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

import java.util.List;

public class LockBLEManager {
    public static final String DEVICE_NAME = "YF-L1";
    public static final int OP_INTERVAL_TIME = 200;
    public static final String PIN_CODE = "123456";
    public static byte GROUP_TYPE = 0;
    public static final byte GROUP_ADMIN = 0;
    public static final byte GROUP_HIJACK = 3;
    public static int OP_TIMEOUT = 20000;
    public static int OPEN_LOCK_FINGERPRINT = 1;
    public static int OPEN_LOCK_PASSWORD = 2;
    public static int OPEN_LOCK_CARD = 3;

    public static void initBle(Application context) {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 1000)
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

    public static void initConfig2(String mac) {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setDeviceName(false, DEVICE_NAME)
                .setDeviceMac(mac)
                .setScanTimeOut(10000);
        BleManager.getInstance().initScanRule(builder.build());
    }

    public static void clear(){
        BleManager.getInstance().disconnectAllDevice();
    }

    public static boolean isConnected(BleDevice bleDevice) {
        return BleManager.getInstance().isConnected(bleDevice);
    }

    public static void setMtu(BleDevice bleDevice) {
        // 设置mtu
        BleManager.getInstance().setMtu(bleDevice, LockBLEPackage.getMtu(), new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
            }

            @Override
            public void onMtuChanged(int mtu) {
                // 设置MTU成功，并获得当前设备传输支持的MTU值
                LockBLEPackage.setMtu(mtu);
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
                    new AlertDialog.Builder(activity)
                            .setTitle("提示")
                            .setMessage("为了更精确的扫描到Bluetooth LE设备, 请打开GPS定位")
                            .setPositiveButton("确定", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivityForResult(intent, REQUEST_GPS);
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
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
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                callbck.onScanStarted();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                callbck.onScanning(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
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
                // 设置连接失败状态
                callbck.onDisconnect(bleDevice);
                ObserverManager.getInstance().notifyObserver(bleDevice);
            }
        });
    }

    public static boolean isFoundDevice(@NonNull String mac){
        boolean isFound = CacheUtil.getCache(mac, DeviceInfo.class) != null;
        if(isFound) return true;

        IndexInfo indexInfo = CacheUtil.getCache(Config.INDEX_DETAIL_URL, IndexInfo.class);
        if(indexInfo != null && indexInfo.getDeviceInfos() != null && indexInfo.getDeviceInfos().size() > 0){
            for(DeviceInfo deviceInfo : indexInfo.getDeviceInfos()){
                if(mac.equals(deviceInfo.getMacAddress())){
                    return true;
                }
            }
        }
        return false;
    }



}
