package com.yc.yfiotlock.ble;

import android.content.Context;
import android.util.Log;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.callback.BleNotifyCallback;
import com.yc.yfiotlock.libs.fastble.callback.BleWriteCallback;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fastble.exception.BleException;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.model.bean.eventbus.IndexReScanEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockReConnectEvent;

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
    private byte mcmd = 0x00;
    private byte scmd = 0x00;
    private byte[] cmdBytes;

    private boolean waupStatus = false;  // 唤醒状态
    private boolean isSend = false;      // 是否发送中
    private boolean isOpOver = false;    // 实际操作是否完成
    private int retryCount = 3;          // 重试次数
    private int wakeUpCount = 0;         // 发送唤醒次数
    private boolean isReInit = false;    // 是否已被初始化
    public int responseErrorCount = 0;   // 响应失败次数

    public LockBLESend(Context context, BleDevice bleDevice) {
        this.context = context;
        this.bleDevice = bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
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

    public boolean isConnected() {
        return LockBLEManager.isConnected(bleDevice);
    }

    public void setMcmd(byte mcmd) {
        this.mcmd = mcmd;
    }

    public void setScmd(byte scmd) {
        this.scmd = scmd;
    }

    // 发送数据
    public void send(byte mcmd, byte scmd, byte[] cmdBytes, boolean iswakeup) {
        this.mcmd = mcmd;
        this.scmd = scmd;
        this.cmdBytes = cmdBytes;
        if (!LockBLEManager.isConnected(bleDevice)) {
            ToastCompat.show(context, "蓝牙已断开");
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
            if (iswakeup) {
                wakeup();
            } else {
                realSend();
            }
        } else {
            Log.d(TAG, "发送未完毕");
        }
    }


    public void realSend() {
        retryCount--;
        if (retryCount < 0) return;
        Log.d(TAG, "直接发送真正指令" + retryCount);
        op(cmdBytes);
        VUiKit.postDelayed(1000 * 2, () -> {
            if (retryCount > 0) {
                if (!isOpOver) {
                    realSend();
                }
            } else {
                retryCount = 3;
                if (!isOpOver) {
                    notifyErrorResponse("no response");
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDeviceChange(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    // 伪发送数据
    public void send(byte mcmd, byte scmd, byte[] cmdBytes) {
        send(mcmd, scmd, cmdBytes, true);
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
        Log.d(TAG, "发送唤醒指令");
        byte[] bytes = LockBLEOpCmd.wakeup(context);
        op(bytes);
        VUiKit.postDelayed(LockBLEManager.OP_INTERVAL_TIME, () -> {
            if (waupStatus && isSend) return;
            if (wakeUpCount++ >= 3) {
                //ToastCompat.show(context, "唤醒门锁失败,无法发送指令");
                Log.d(TAG, "唤醒门锁失败,无法发送指令");
                wakeUpCount = 0;
                wakeupFailureResponse();
                return;
            }
            wakeup();
        });
    }

    // 监听
    private static int notifyRetryCount = 3;
    private static boolean isNotityReady = false;

    public static boolean isNotityReady() {
        return isNotityReady;
    }

    public static void bleNotify(BleDevice bleDevice) {
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
                        EventBus.getDefault().post(new BleNotifyEvent(BleNotifyEvent.onNotifySuccess));
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.d(TAG, "回调通失败:" + exception.getDescription());
                        isNotityReady = false;
                        if (notifyRetryCount-- > 0) {
                            VUiKit.postDelayed(notifyRetryCount * (1000 - notifyRetryCount * 200), () -> {
                                bleNotify(bleDevice);
                            });
                        } else {
                            notifyRetryCount = 3;
                            EventBus.getDefault().post(new BleNotifyEvent(BleNotifyEvent.onNotifyFailure));
                        }
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
            Log.d(TAG, "非正常响应:" + "mscd:" + lockBLEData.getMcmd() + " scmd:" + lockBLEData.getScmd() + " mscd:" + mcmd + " scmd:" + scmd);
            return;
        }
        if (lockBLEData.getMcmd() == LockBLEOpCmd.MCMD && lockBLEData.getScmd() == LockBLEOpCmd.SCMD_WAKE_UP) {
            // 唤醒成功后发送真正操作
            Log.d(TAG, "唤醒状态:" + lockBLEData.getStatus());
            if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_OK) {
                if (!waupStatus) {
                    waupStatus = true;
                    Log.d(TAG, "唤醒成功,发送真正指令");
                    op(cmdBytes);
                }
            } else if (lockBLEData.getStatus() == LockBLEBaseCmd.STATUS_KEY_ERROR) {
                // 密钥不对 设备重新初始化
                isReInit = true;
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
    }

    //  bleDevice 出现未知问题
    private void rescan() {
        if (responseErrorCount > 3) {
            Log.d(TAG, "超过最大错误次数:" + responseErrorCount + " 重新搜索连接");
            LockBLEManager.destory();
            EventBus.getDefault().post(new IndexReScanEvent());
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

    // 响应错误
    public void notifyErrorResponse(String error) {
        Log.d(TAG, "响应超时");
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setOther(error.getBytes());
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
