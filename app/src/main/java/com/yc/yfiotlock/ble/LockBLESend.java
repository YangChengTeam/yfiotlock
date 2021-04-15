package com.yc.yfiotlock.ble;

import android.content.Context;
import android.util.Log;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.callback.BleNotifyCallback;
import com.yc.yfiotlock.libs.fastble.callback.BleWriteCallback;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fastble.exception.BleException;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockReConnectEvent;
import com.yc.yfiotlock.model.bean.eventbus.ReScanEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LockBLESend {
    private static final String TAG = "LockBleSend";

    public static final String WRITE_SERVICE_UUID = "55535343-fe7d-4ae5-8fa9-9fafd205e455";
    public static final String NOTIFY_SERVICE_UUID = "55535343-fe7d-4ae5-8fa9-9fafd205e455";
    public static final String WRITE_CHARACTERISTIC_UUID = "49535343-8841-43f4-a8d4-ecbe34729bb3";
    public static final String NOTIFY_CHARACTERISTIC_UUID = "49535343-1e4d-4bd9-ba61-23c647249616";

    private Context context;
    private BleDevice bleDevice;
    private String key;
    private byte mcmd = 0x00;
    private byte scmd = 0x00;
    private byte[] cmdBytes;

    public static final int DEFAULT_RETRY_COUNT = 2;
    private boolean waupStatus = false;  // 唤醒状态
    private boolean isSend = false;      // 是否发送中
    private boolean isOpOver = false;    // 实际操作是否完成
    private int wakeUpCount = 0;         // 发送唤醒次数
    private boolean isReInit = false;    // 是否已被初始化
    public int responseErrorCount = 0;   // 响应失败次数

    public LockBLESend(Context context, BleDevice bleDevice, String key) {
        this.context = context;
        this.bleDevice = bleDevice;
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

    public boolean isReInit() {
        return isReInit;
    }

    private boolean isConnected() {
        return LockBLEManager.getInstance().isConnected(bleDevice);
    }

    public void setMcmd(byte mcmd) {
        this.mcmd = mcmd;
    }

    public void setScmd(byte scmd) {
        this.scmd = scmd;
    }

    // 发送数据
    public void send(byte mcmd, byte scmd, byte[] cmdBytes) {
        this.mcmd = mcmd;
        this.scmd = scmd;
        this.cmdBytes = cmdBytes;
        if (!isConnected()) {
            notifyNoConnectResponse("ble no connection!");
            EventBus.getDefault().post(new OpenLockReConnectEvent());
            return;
        } else {
            if (!LockBLESend.isNotityReady()) {
                LockBLESend.bleNotify(bleDevice);
                return;
            }
        }
        if (!isSend) {
            Log.d(TAG, "正在发送");
            isSend = true;
            isOpOver = false;
            wakeup();
        } else {
            Log.d(TAG, "发送未完毕");
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

    // 持续唤醒
    private void wakeup() {
        if (waupStatus && isSend) return;
        if (isOpOver) return;
        Log.d(TAG, "发送唤醒指令");
        byte[] bytes = LockBLEOpCmd.wakeup(key);
        op(bytes);
        VUiKit.postDelayed(LockBLEManager.OP_INTERVAL_TIME, () -> {
            if (isOpOver) return;
            if (waupStatus && isSend) return;
            if (wakeUpCount++ >= DEFAULT_RETRY_COUNT) {
                Log.d(TAG, "唤醒门锁失败,无法发送指令");
                wakeUpCount = 0;
                wakeupFailureResponse();
                return;
            }
            wakeup();
        });
    }

    // 监听
    private static boolean isNotityReady = false; // 通知是否注册成功
    public static boolean isNotityReady() {
        return isNotityReady;
    }
    private static boolean isRegNotifying = false; // 是否正在注册通知
    public static void bleNotify(BleDevice bleDevice) {
        if(isRegNotifying) return;
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
                        LockBLEManager.getInstance().getScannedBleDevices().remove(bleDevice.getMac());
                        EventBus.getDefault().post(new ReScanEvent());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.d(TAG, "响应数据:" + LockBLEUtils.toHexString(data));
                        // 解析响应
                        LockBLEData lockBLEData = LockBLEPackage.getData(data);
                        if (lockBLEData != null) {
                            Log.d(TAG, "解析成功:" + "mscd:" + lockBLEData.getMcmd() + " scmd:" + lockBLEData.getScmd() + " status:" + lockBLEData.getStatus());
                            EventBus.getDefault().post(lockBLEData);
                        } else {
                            Log.d(TAG, "解析失败");
                            EventBus.getDefault().post(new BleNotifyEvent(BleNotifyEvent.onNotifyChangeFailure, "lockBLEData format error"));
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotify(BleNotifyEvent bleNotifyEvent) {
        if (bleNotifyEvent.getStatus() == BleNotifyEvent.onNotifyChangeFailure) {
            notifyErrorResponse(bleNotifyEvent.getDesp());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifySuccess(LockBLEData lockBLEData) {
        processNotify(lockBLEData);
    }

    // 处理响应
    private void processNotify(LockBLEData lockBLEData) {
        if (mcmd == 0x00 || scmd == 0x00) {
            Log.d(TAG, "非正常响应:" + "mscd:" + lockBLEData.getMcmd() + " scmd:" + lockBLEData.getScmd() + " mscd:" + mcmd + " scmd:" + scmd + " status:" + lockBLEData.getStatus());
            return;
        }
        if (lockBLEData.getMcmd() == LockBLEOpCmd.MCMD && lockBLEData.getScmd() == LockBLEOpCmd.SCMD_WAKE_UP) {
            // 唤醒成功后发送真正操作
            if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                if (!waupStatus) {
                    waupStatus = true;
                    Log.d(TAG, "唤醒成功,发送真正指令");
                    op(cmdBytes);
                }
            } else if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_KEY_ERROR) {
                // 密钥不对 设备已重新初始化
                isReInit = true;
            } else {
                Log.d(TAG, "唤醒失败");
            }
        } else if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            if (lockBLEData.getScmd() != LockBLEEventCmd.SCMD_FINGERPRINT_INPUT_COUNT) {
                reset();
                if (notifyCallback != null) {
                    if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                        notifyCallback.onNotifySuccess(lockBLEData);
                    } else {
                        notifyCallback.onNotifyFailure(lockBLEData);
                    }
                }
            } else {
                if (notifyCallback != null) {
                    if (lockBLEData.getStatus() >= 0 && lockBLEData.getStatus() <= (byte) 0x06) {
                        notifyCallback.onNotifySuccess(lockBLEData);
                    } else {
                        reset();
                        notifyCallback.onNotifyFailure(lockBLEData);
                    }
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
                Log.d(TAG, "响应失败状态:" + lockBLEData.getStatus());
                if (notifyCallback != null) {
                    notifyCallback.onNotifyFailure(lockBLEData);
                }
            }
        } else {
            if (waupStatus) {
                reset();
                if (notifyCallback != null) {
                    notifyCallback.onNotifyFailure(lockBLEData);
                }
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
        waupStatus = false;
        mcmd = 0x00;
        scmd = 0x00;
        cmdBytes = null;
        wakeUpCount = 0;
    }

    //  bleDevice 出现未知问题
    private void rescan() {
        if (responseErrorCount > 3) {
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
        Log.d(TAG, "蓝牙未连接");
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setExtra(error.getBytes());
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_NOTIFY_NO_CONNECTION);
        processNotify(lockBLEData);
    }

    // 响应错误
    public void notifyErrorResponse(String error) {
        Log.d(TAG, "响应超时");
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setExtra(error.getBytes());
        lockBLEData.setStatus(LockBLEBaseCmd.STATUS_NOTIFY_TIMEOUT_ERROR);
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
                        Log.d(TAG, "写入数据:" + LockBLEUtils.toHexString(justWrite));
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
