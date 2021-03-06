package com.yc.yfiotlock.ble;

import android.app.Application;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

public class LockBLEManager {

    public static byte GROUP_TYPE = 0;
    public static final byte GROUP_ADMIN = 0;
    public static final byte GROUP_HIJACK = 3;
    public static int OP_TIMEOUT = 1000;
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
        initConfig();
    }

    private static void initConfig() {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setDeviceMac("7D:B5:97:58:AB:92")
                .setScanTimeOut(10000);
        BleManager.getInstance().initScanRule(builder.build());
    }

    public static void setMtu(BleDevice bleDevice){
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
}
