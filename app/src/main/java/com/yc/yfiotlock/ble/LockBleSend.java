package com.yc.yfiotlock.ble;

import android.content.Context;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.constant.Config;

import org.greenrobot.eventbus.EventBus;

public class LockBleSend {
    private static final String TAG = "LockBleSend";

    public static final String WRITE_SERVICE_UUID = "55535343-fe7d-4ae5-8fa9-9fafd205e455";
    public static final String NOTIFY_SERVICE_UUID = "5833ff01-9b8b-5191-6142-22a4536ef123";
    public static final String WRITE_CHARACTERISTIC_UUID = "49535343-8841-43f4-a8d4-ecbe34729bb3";
    public static final String NOTIFY_CHARACTERISTIC_UUID = "5833ff03-9b8b-5191-6142-22a4536ef123";

    private Context context;
    private BleDevice bleDevice;

    private byte mcmd = 0x00;
    private byte scmd = 0x00;
    private byte[] cmdBytes;

    private boolean waupStatus = false;
    private boolean sendingStatus = false;

    public LockBleSend(Context context, BleDevice bleDevice) {
        this.context = context;
        this.bleDevice = bleDevice;
        bleNotify();
    }

    public void send(byte mcmd, byte scmd, byte[] cmdBytes) {
        if (!sendingStatus) {
            sendingStatus = true;
            this.mcmd = mcmd;
            this.scmd = scmd;
            this.cmdBytes = cmdBytes;
            wakeup();

            VUiKit.postDelayed(Config.OP_TIMEOUT, () -> {
                if (sendingStatus) {
                    sendingStatus = false;
                    notifyTimeoutResponse();
                }
            });
        }
    }

    public void clear() {
        BleManager.getInstance().removeNotifyCallback(bleDevice, NOTIFY_CHARACTERISTIC_UUID);
    }

    private void wakeup() {
        if (!waupStatus) {
            byte[] bytes = LockBLEOpCmd.wakeup(context);
            op(bytes);
        }
        VUiKit.postDelayed(50, () -> {
            Log.d(TAG, "唤醒门锁中");
            wakeup();
        });
    }

    private void bleNotify() {
        BleManager.getInstance().notify(
                bleDevice,
                NOTIFY_SERVICE_UUID,
                NOTIFY_CHARACTERISTIC_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.d(TAG, exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.d(TAG, "响应数据:" + LockBLEUtil.toHexString(data));
                        // 解析响应
                        LockBLEData lockBLEData = LockBLEPackage.getData(data);
                        if (lockBLEData != null) {
                            processNotify(lockBLEData);
                        }
                    }
                });
    }

    // 处理响应
    private void processNotify(LockBLEData lockBLEData) {
        // 唤醒成功
        if (lockBLEData.getMcmd() == (byte) 0x02 && lockBLEData.getScmd() == (byte) 0x0B) {
            waupStatus = true;
            op(cmdBytes); // 唤醒成功后发送真正操作
        } else if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            EventBus.getDefault().post(lockBLEData);
            sendingStatus = false;
        }
    }

    private void writeFailureResponse() {
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setStatus((byte) 0x10);  // 写入失败
        EventBus.getDefault().post(lockBLEData);
    }

    private void notifyTimeoutResponse() {
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setStatus((byte) 0x11);  // 响应超时
        EventBus.getDefault().post(lockBLEData);
    }

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
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        writeFailureResponse();
                    }
                });
    }
}
