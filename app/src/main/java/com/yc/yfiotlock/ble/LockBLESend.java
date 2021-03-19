package com.yc.yfiotlock.ble;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.yc.yfiotlock.libs.fastble.BleManager;
import com.yc.yfiotlock.libs.fastble.callback.BleNotifyCallback;
import com.yc.yfiotlock.libs.fastble.callback.BleWriteCallback;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.libs.fastble.exception.BleException;
import com.kk.utils.VUiKit;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.controller.dialogs.LoadingDialog;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockReConnectEvent;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockReSendEvent;

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
    private LoadingDialog loadingDialog;
    private byte mcmd = 0x00;
    private byte scmd = 0x00;
    private byte[] cmdBytes;

    private boolean waupStatus = false;
    private boolean isSend = false;

    public LockBLESend(Context context, BleDevice bleDevice) {
        this.context = context;
        this.bleDevice = bleDevice;
        loadingDialog = new LoadingDialog(context);
        // 开始监听
        bleNotify();
    }

    // 伪发送数据
    public void send(byte mcmd, byte scmd, byte[] cmdBytes) {
        if (!LockBLEManager.isConnected(bleDevice)) {
            GeneralDialog generalDialog = new GeneralDialog(context);
            generalDialog.setTitle("温馨提示");
            generalDialog.setMsg("设备已断开, 重新连接");
            generalDialog.setOnPositiveClickListener(new GeneralDialog.OnBtnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    connect();
                }
            });
            generalDialog.show();
            return;
        }
        if (!isSend) {
            isSend = true;
            this.mcmd = mcmd;
            this.scmd = scmd;
            this.cmdBytes = cmdBytes;
            wakeup();
        }
    }

    public void connect() {
        LockBLEManager.connect(bleDevice, new LockBLEManager.LockBLEConnectCallbck() {
            @Override
            public void onConnectStarted() {
                loadingDialog.show("正在连接");
            }

            @Override
            public void onDisconnect(BleDevice bleDevice) {

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice) {
                loadingDialog.dismiss();
                send();
            }

            @Override
            public void onConnectFailed() {
                loadingDialog.dismiss();
                send();
            }
        });
    }

    public interface NotifyCallback {
        void onSuccess(byte[] data);

        void onFailure(byte status, String error);
    }

    private NotifyCallback notifyCallback;

    public void setNotifyCallback(NotifyCallback notifyCallback) {
        this.notifyCallback = notifyCallback;
    }

    private void send() {
        if (mcmd == 0x00 || scmd == 0x00 || cmdBytes == null) {
            return;
        }
        send(mcmd, scmd, cmdBytes);
    }

    // 清除操作
    public void clear() {
        BleManager.getInstance().removeNotifyCallback(bleDevice, NOTIFY_CHARACTERISTIC_UUID);
    }

    // 持续唤醒
    private void wakeup() {
        if (waupStatus) return;
        byte[] bytes = LockBLEOpCmd.wakeup(context);
        op(bytes);
        VUiKit.postDelayed(LockBLEManager.OP_INTERVAL_TIME, () -> {
            if (waupStatus) return;
            Log.d(TAG, "唤醒门锁中");
            wakeup();
        });
    }

    // 监听
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
                        notifyErrorResponse(exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.d(TAG, "响应数据:" + LockBLEUtils.toHexString(data));
                        // 解析响应
                        LockBLEData lockBLEData = LockBLEPackage.getData(data);
                        if (lockBLEData != null) {
                            processNotify(lockBLEData);
                        } else {
                            notifyErrorResponse("lockBLEData format error");
                        }
                    }
                });
    }

    // 处理响应
    private void processNotify(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == (byte) 0x02 && lockBLEData.getScmd() == (byte) 0x0B) {
            // 唤醒成功后发送真正操作
            if (lockBLEData.getStatus() == (byte) 0x00) {
                if (!waupStatus) {
                    waupStatus = true;
                    op(cmdBytes);
                }
            } else {
                Log.d(TAG, "唤醒失败");
            }
        } else if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            // 操作响应
            Log.d(TAG, "命令匹配");

            if (lockBLEData.getStatus() == (byte) 0x00) {
                if (notifyCallback != null) {
                    notifyCallback.onSuccess(lockBLEData.getOther());
                }
            } else if (lockBLEData.getStatus() != (byte) 0x01) {
                if (notifyCallback != null) {
                    notifyCallback.onFailure((byte) 0x01, "cmd failure");
                }
            } else if (lockBLEData.getStatus() == (byte) 0x10) {
                if (notifyCallback != null) {
                    notifyCallback.onFailure((byte) 0x10, new String(lockBLEData.getOther()));
                }
            } else if (lockBLEData.getStatus() == (byte) 0x11) {
                if (notifyCallback == null) {
                    notifyCallback.onFailure((byte) 0x11, new String(lockBLEData.getOther()));
                }
            }
            reset();
        } else {
            Log.d(TAG, "命令不匹配:" + LockBLEUtils.toHexString(lockBLEData.build(context)));
        }

    }

    // 重置所有变量
    private void reset() {
        isSend = false;
        waupStatus = false;
        mcmd = 0x00;
        scmd = 0x00;
        cmdBytes = null;
    }

    // 写入失败
    private void writeFailureResponse() {
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setStatus((byte) 0x10);
        processNotify(lockBLEData);
    }

    // 响应超时
    private void notifyErrorResponse(String error) {
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(mcmd);
        lockBLEData.setScmd(scmd);
        lockBLEData.setOther(error.getBytes());
        lockBLEData.setStatus((byte) 0x11);
        processNotify(lockBLEData);
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
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        writeFailureResponse();
                    }
                });
    }
}
