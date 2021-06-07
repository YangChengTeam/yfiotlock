package com.yc.yfiotlock.ble;

import android.content.Context;
import android.util.Log;

import com.kk.utils.LogUtil;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.callback.BleNotifyCallback;
import com.yc.yfiotlock.libs.fastble.callback.BleWriteCallback;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fastble.exception.BleException;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.model.bean.eventbus.ForamtErrorEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockReConnectEvent;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

public class LockBLESender {
    private static final String TAG = "LockBleSend";

    public static final String WRITE_SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String NOTIFY_SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String WRITE_CHARACTERISTIC_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String NOTIFY_CHARACTERISTIC_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";

    //private Context context;
    private BleDevice bleDevice;
    private String key;
    private byte mcmd = 0x00;
    private byte scmd = 0x00;
    private byte[] cmdBytes;

    public static final int DEFAULT_RETRY_COUNT = 3; // 实际默认次数3
    private boolean wakeupStatus = false;  // 唤醒状态
    private boolean isSend = false;      // 是否发送中
    private boolean isOpOver = false;    // 实际操作是否完成
    private int wakeupCount = 0;         // 发送唤醒次数
    public int responseErrorCount = 0;   // 响应失败次数


    public LockBLESender(Context context, BleDevice bleDevice, String key) {
        //this.context = context;
        this.bleDevice = bleDevice;
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public boolean isOpOver() {
        return isOpOver;
    }

    public void setOpOver(boolean opOver) {
        isOpOver = opOver;
    }

    private boolean isConnected() {
        return (mcmd == LockBLESettingCmd.MCMD && scmd == LockBLESettingCmd.SCMD_CHECK_LOCK) || LockBLEManager.getInstance().isConnected(bleDevice);
    }

    public void setMcmd(byte mcmd) {
        this.mcmd = mcmd;
    }

    public void setScmd(byte scmd) {
        this.scmd = scmd;
    }

    // 发送数据
    public void send(byte mcmd, byte scmd, byte[] cmdBytes) {
        send(mcmd, scmd, cmdBytes, true);
    }

    public void send(byte mcmd, byte scmd, byte[] cmdBytes, boolean isWakeup) {
        this.mcmd = mcmd;
        this.scmd = scmd;
        this.cmdBytes = cmdBytes;
        if (!isConnected()) {
            notifyNoConnectResponse("ble disconnection!");
            EventBus.getDefault().post(new OpenLockReConnectEvent());
            return;
        } else {
            if (!LockBLESender.isNotityReady()) {
                LockBLESender.bleNotify(bleDevice);
                return;
            }
        }
        if (!isSend) {
            Log.d(TAG, "正在发送");
            isSend = true;
            isOpOver = false;
            if (isWakeup) {
                wakeupStatus = false;
                wakeup();
            } else {
                Log.d(TAG, "直接发送真正指令");
                wakeupStatus = true;
                op(cmdBytes);
            }
        } else {
            Log.d(TAG, "指令未发送完毕");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDeviceChange(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public interface NotifyCallback {
        void onNotifySuccess(LockBLEData lockBLEData);

        void onNotifyFailure(LockBLEData lockBLEData);
    }

    private NotifyCallback notifyCallback;

    public void setNotifyCallback(NotifyCallback notifyCallback) {
        this.notifyCallback = notifyCallback;
    }

    // 清除操作
    public void clear() {
        LockBLEManager.getInstance().disConnect(bleDevice);
    }

    public void registerNotify() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    // 清除eventbug
    public void unregisterNotify() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    private long timestamp  = 0;
    // 持续唤醒
    private void wakeup() {
        Log.d(TAG, "发送间隔:" + (System.currentTimeMillis() - timestamp));
        if(System.currentTimeMillis() - timestamp < LockBLEManager.OP_INTERVAL_TIME) return;
        if (wakeupStatus && isSend) return;
        if (isOpOver) return;
        Log.d(TAG, "发送唤醒指令");
        byte[] bytes = LockBLEOpCmd.wakeup(key);
        op(bytes);
        timestamp = System.currentTimeMillis();
        WakeUpRunnable wakeUpRunnable = new WakeUpRunnable(Arrays.hashCode(cmdBytes));
        VUiKit.postDelayed(LockBLEManager.OP_INTERVAL_TIME, wakeUpRunnable);
    }

    private class WakeUpRunnable implements Runnable {
        private final int hashCode;

        public WakeUpRunnable(int hashCode) {
            this.hashCode = hashCode;
        }

        @Override
        public void run() {
            if (LockBLESender.this.cmdBytes == null || this.hashCode != Arrays.hashCode(LockBLESender.this.cmdBytes))
                return;
            if (isOpOver) return;
            if (wakeupStatus && isSend) return;
            if (++wakeupCount >= DEFAULT_RETRY_COUNT) {
                Log.d(TAG, "唤醒门锁失败,无法发送真正指令");
                wakeupCount = 0;
                wakeupFailureResponse();
                return;
            }
            wakeup();
        }
    }

    // 监听
    private static boolean isNotityReady = false; // 通知是否注册成功

    public static boolean isNotityReady() {
        return isNotityReady;
    }

    private static boolean isRegNotifying = false; // 是否正在注册通知

    public static void bleNotify(BleDevice bleDevice) {
        if (isRegNotifying) return;
        isRegNotifying = true;
        BleManager.getInstance().removeNotifyCallback(bleDevice, NOTIFY_SERVICE_UUID);
        BleManager.getInstance().notify(
                bleDevice,
                NOTIFY_SERVICE_UUID,
                NOTIFY_CHARACTERISTIC_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.d(TAG, "回调通知成功");
                        isNotityReady = true;
                        isRegNotifying = false;
                        EventBus.getDefault().post(new BleNotifyEvent(BleNotifyEvent.onNotifySuccess));
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.d(TAG, "回调通失败:" + exception.getDescription());
                        isNotityReady = false;
                        isRegNotifying = false;
                        EventBus.getDefault().post(new BleNotifyEvent(BleNotifyEvent.onNotifyFailure));
                        LockBLEManager.getInstance().disConnect(bleDevice);
                        EventBus.getDefault().post(new ReScanEvent());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.d(TAG, "响应数据:" + LockBLEUtil.toHexString(data));
                        // 解析响应
                        EventBus.getDefault().post(data);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifySuccess(byte[] data) {
        if (mcmd == 0x00 || scmd == 0x00) {
            return;
        }
        LockBLEData lockBLEData = LockBLEPackage.getData(data, key);
        if (lockBLEData == null || lockBLEData.getMcmd() == (byte) 0x00 || lockBLEData.getScmd() == (byte) 0x00) {
            lockBLEData = LockBLEPackage.getData(data);
            Log.d(TAG, "非加密状态:" + " mcmd:" + mcmd + " scmd:" + scmd);
        } else {
            Log.d(TAG, "加密状态:" + " mcmd:" + mcmd + " scmd:" + scmd);
        }
        if (lockBLEData == null || lockBLEData.getMcmd() == (byte) 0x00 || lockBLEData.getScmd() == (byte) 0x00) {
            EventBus.getDefault().post(new ForamtErrorEvent());
        } else {
            processNotify(lockBLEData);
        }
    }

    // 处理响应
    private void processNotify(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEOpCmd.MCMD && lockBLEData.getScmd() == LockBLEOpCmd.SCMD_WAKE_UP) {
            // 唤醒成功后发送真正操作
            if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                if (!wakeupStatus) {
                    wakeupStatus = true;
                    Log.d(TAG, "唤醒成功,发送真正指令");
                    op(cmdBytes);
                }
            } else {
                Log.d(TAG, "唤醒失败");
            }
        } else if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            if (lockBLEData.getScmd() != LockBLEEventCmd.SCMD_FINGERPRINT_INPUT_COUNT) {
                reset();
            }
            if (notifyCallback != null) {
                if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                    notifyCallback.onNotifySuccess(lockBLEData);
                } else {
                    notifyCallback.onNotifyFailure(lockBLEData);
                }
            }
        } else if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            isOpOver = true;
            Log.d(TAG, "命令匹配:" + "mscd:" + lockBLEData.getMcmd() + " scmd:" + lockBLEData.getScmd() + " status:" + lockBLEData.getStatus());
            // 操作响应
            reset();
            if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                if (notifyCallback != null) {
                    notifyCallback.onNotifySuccess(lockBLEData);
                }
            } else {
                if (notifyCallback != null) {
                    notifyCallback.onNotifyFailure(lockBLEData);
                }
            }
        } else {
            if (wakeupStatus) {
                reset();
                Log.d(TAG, "命令不匹配:" + "mscd:" + lockBLEData.getMcmd() + " scmd:" + lockBLEData.getScmd());
            } else {
                Log.d(TAG, "未唤醒命令不匹配:" + "mscd:" + lockBLEData.getMcmd() + mcmd + " scmd:" + lockBLEData.getScmd() + "-" + scmd);
            }
        }
    }

    // 重置所有变量
    private void reset() {
        Log.d(TAG, "重置命令完毕");
        isSend = false;
        wakeupStatus = false;
        mcmd = 0x00;
        scmd = 0x00;
        cmdBytes = null;
        wakeupCount = 0;
    }

    //  bleDevice 出现未知问题
    private void rescan() {
        if (responseErrorCount > 3) {
            responseErrorCount = 0;
            Log.d(TAG, "超过最大错误次数:" + responseErrorCount + " 重新搜索连接");
            LockBLEManager.getInstance().destory();
            EventBus.getDefault().post(new ReScanEvent());
        }
    }

    // 写入失败
    private void writeFailureResponse() {
        Log.d(TAG, "写入失败");
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_WRITE_ERROR);
        processNotify(lockBLEData);
        responseErrorCount++;
        rescan();
    }

    // 蓝牙未连接
    public void notifyNoConnectResponse(String error) {
        Log.d(TAG, error);
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setExtra(error.getBytes());
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_NOTIFY_NO_CONNECTION);
        responseErrorCount++;
        processNotify(lockBLEData);
    }

    // 响应错误
    public void notifyErrorResponse(String error) {
        Log.d(TAG, error);
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setExtra(error.getBytes());
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_NOTIFY_TIMEOUT_ERROR);
        processNotify(lockBLEData);
        responseErrorCount++;
        rescan();
    }

    // 响应错误
    public void notifyFormatError(String error) {
        Log.d(TAG, error);
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setExtra(error.getBytes());
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_KEY_ERROR);
        processNotify(lockBLEData);
        responseErrorCount++;
        rescan();
    }

    // 唤醒失败
    private void wakeupFailureResponse() {
        Log.d(TAG, "唤醒失败");
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_WAKEUP_ERROR);
        processNotify(lockBLEData);
        responseErrorCount++;
        rescan();
    }

    // 写入操作
    private void op(byte[] bytes) {
        BleManager.getInstance().write(
                bleDevice,
                WRITE_SERVICE_UUID,
                WRITE_CHARACTERISTIC_UUID,
                bytes,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        Log.d(TAG, "写入数据:" + LockBLEUtil.toHexString(justWrite));
                        responseErrorCount = 0;
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        Log.d(TAG, "写入数据:" + exception.getDescription());
                        writeFailureResponse();
                    }
                });
    }
}
