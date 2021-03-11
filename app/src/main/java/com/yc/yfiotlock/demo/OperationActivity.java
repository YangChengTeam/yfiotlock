package com.yc.yfiotlock.demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLEPackage;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.demo.comm.ObserverManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.yc.yfiotlock.demo.MainActivity.SERVICE_UUID;

public class OperationActivity extends AppCompatActivity implements View.OnClickListener {
    BleDevice bleDevice;

    ProgressDialog progressDialog;
    byte[] bytes = new byte[]{(byte) 0xAA, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x00, (byte) 0x0B, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x23, (byte) 0x0B, (byte) 0xC3, (byte) 0x8B, (byte) 0xBB};

    public static final String WRITE_SERVICE_UUID = "55535343-fe7d-4ae5-8fa9-9fafd205e455";
    public static final String NOTIFY_SERVICE_UUID = "5833ff01-9b8b-5191-6142-22a4536ef123";
    public static final String WRITE_CHARACTERISTIC_UUID = "49535343-8841-43f4-a8d4-ecbe34729bb3";
    public static final String NOTIFY_CHARACTERISTIC_UUID = "5833ff03-9b8b-5191-6142-22a4536ef123";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_operation);


        progressDialog = new ProgressDialog(this);

        bleDevice = getIntent().getParcelableExtra("bleDevice");
        TextView blenameTv = findViewById(R.id.tv_blename);
        blenameTv.setText("蓝牙名称:" + bleDevice.getName());

        TextView notifyTv = findViewById(R.id.tv_notify);

        BleManager.getInstance().notify(
                bleDevice,
                NOTIFY_SERVICE_UUID,
                NOTIFY_CHARACTERISTIC_UUID,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        //Toast.makeText(OperationActivity.this, "Notify成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Toast.makeText(OperationActivity.this, "Notify失败:" + exception.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        bytes = data;
                        notifyTv.setText(LockBLEUtil.toHexString(data) + "");
                        Toast.makeText(OperationActivity.this, "Notify响应:" + LockBLEUtil.toHexString(data), Toast.LENGTH_LONG).show();
                    }
                });

        CheckBox checkBox = findViewById(R.id.ck_aes);
        checkBox.setChecked(LockBLEData.isAesData);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LockBLEData.isAesData = isChecked;
            }
        });

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ble-notify", notifyTv.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(OperationActivity.this, "复制成功", Toast.LENGTH_LONG).show();

            }
        });

        findViewById(R.id.btn_ansy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockBLEData data = LockBLEPackage.getData(bytes);
                if (data != null) {
                    notifyTv.setText(LockBLEUtil.toHexString(new byte[]{data.getMcmd(), data.getScmd(), data.getStatus()})
                            + "");
                    Toast.makeText(OperationActivity.this, "解析结果:" + LockBLEUtil.toHexString(new byte[]{data.getMcmd(), data.getScmd(), data.getStatus()}), Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(this);
        findViewById(R.id.btn_wifi).setOnClickListener(this);
        findViewById(R.id.btn_bind_ble).setOnClickListener(this);
        findViewById(R.id.btn_verid).setOnClickListener(this);
        findViewById(R.id.btn_synctime).setOnClickListener(this);
        findViewById(R.id.btn_setkey).setOnClickListener(this);
        findViewById(R.id.btn_cancel_ble).setOnClickListener(this);
        findViewById(R.id.btn_cancel_wifi).setOnClickListener(this);
        findViewById(R.id.btn_change_volume).setOnClickListener(this);


        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_add_pwd).setOnClickListener(this);
        findViewById(R.id.btn_mod_pwd).setOnClickListener(this);
        findViewById(R.id.btn_del_pwd).setOnClickListener(this);
        findViewById(R.id.btn_add_card).setOnClickListener(this);
        findViewById(R.id.btn_mod_card).setOnClickListener(this);
        findViewById(R.id.btn_del_card).setOnClickListener(this);
        findViewById(R.id.btn_add_fp).setOnClickListener(this);
        findViewById(R.id.btn_mod_fp).setOnClickListener(this);
        findViewById(R.id.btn_del_fp).setOnClickListener(this);
        findViewById(R.id.btn_wake_up).setOnClickListener(this);
    }

    private void op(byte[] bytes) {
        String message = LockBLEUtil.toHexString(bytes);
        AlertDialog.Builder builder = new AlertDialog.Builder(OperationActivity.this);
        builder.setTitle("模拟数据如下:");
        builder.setMessage(message);
        builder.setPositiveButton("写入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleManager.getInstance().write(
                        bleDevice,
                        WRITE_SERVICE_UUID,
                        WRITE_CHARACTERISTIC_UUID,
                        bytes,
                        new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(OperationActivity.this, "Write成功: " + HexUtil.formatHexString(justWrite, true).toUpperCase(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onWriteFailure(final BleException exception) {
                                Toast.makeText(OperationActivity.this, "Write失败: " + exception.getDescription(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("复制", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ble", message);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(OperationActivity.this, "复制成功", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            progressDialog.setMessage("重新连接中...");
            progressDialog.show();

            BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                @Override
                public void onStartConnect() {
                    try {
                        if (!progressDialog.isShowing()) {
                            progressDialog.setMessage("重新连接中...");
                            progressDialog.show();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    progressDialog.dismiss();
                    Toast.makeText(OperationActivity.this, bleDevice.getName() + "连接失败" + exception.getDescription(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    progressDialog.dismiss();
                    Toast.makeText(OperationActivity.this, bleDevice.getName() + "连接成功", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                    if (isActiveDisConnected) {
                        Toast.makeText(OperationActivity.this, bleDevice.getName() + getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(OperationActivity.this, bleDevice.getName() + getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                        ObserverManager.getInstance().notifyObserver(bleDevice);
                    }
                }
            });
            return;
        }
        switch (v.getId()) {
            case R.id.btn_reset: {
                byte[] bytes = LockBLESettingCmd.reset(this);
                op(bytes);
                break;
            }
            case R.id.btn_wifi: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("配网:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText ssidET = new EditText(this);
                ssidET.setText("YFHome");
                layout.addView(ssidET);

                final EditText pwdEt = new EditText(this);
                pwdEt.setText("YFHome168");
                layout.addView(pwdEt);
                dialog.setView(layout);

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String ssid = ssidET.getText().toString();
                        String pwd = pwdEt.getText().toString();
                        byte[] bytes = LockBLESettingCmd.wiftDistributionNetwork(OperationActivity.this, ssid, pwd);
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }
            case R.id.btn_bind_ble: {
                byte[] bytes = LockBLESettingCmd.bindBle(this);
                op(bytes);
                break;
            }
            case R.id.btn_verid: {
                byte[] bytes = LockBLESettingCmd.verifyIdentidy(this);
                op(bytes);
                break;
            }
            case R.id.btn_synctime: {
                byte[] bytes = LockBLESettingCmd.syncTime(OperationActivity.this);
                op(bytes);
                break;
            }

            case R.id.btn_setkey: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("设置新密钥:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText keyET = new EditText(this);
                keyET.setText("2345678");
                layout.addView(keyET);

                dialog.setView(layout);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String key = keyET.getText().toString();
                        byte[] bytes = LockBLESettingCmd.setAesKey(OperationActivity.this, key, "12345678");
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_cancel_wifi: {
                byte[] bytes = LockBLESettingCmd.cancelWifi(OperationActivity.this);
                op(bytes);
                break;
            }

            case R.id.btn_cancel_ble: {
                byte[] bytes = LockBLESettingCmd.cancelBle(OperationActivity.this);
                op(bytes);
                break;
            }

            case R.id.btn_open: {
                byte[] bytes = LockBLEOpCmd.open(OperationActivity.this);
                op(bytes);
                break;
            }

            case R.id.btn_change_volume: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("设置音量:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText volumeET = new EditText(this);
                volumeET.setText("1");
                layout.addView(volumeET);

                dialog.setView(layout);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String volume = volumeET.getText().toString();
                        byte[] bytes = LockBLESettingCmd.changeVolume(OperationActivity.this, Integer.valueOf(volume));
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_add_pwd: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("添加密码:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText pwdEt = new EditText(this);
                pwdEt.setText("123456");
                layout.addView(pwdEt);
                dialog.setView(layout);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String type = typeET.getText().toString();
                        String pwd = pwdEt.getText().toString();
                        byte[] bytes = LockBLEOpCmd.addPwd(OperationActivity.this, Byte.valueOf(type), "00000001", pwd, new byte[]{00, 00, 00, 00, 00, 00}, new byte[]{00, 00, 00, 00, 00, 00});
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_mod_pwd: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("修改密码:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);


                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

                final EditText pwdEt = new EditText(this);
                pwdEt.setText("123456");
                layout.addView(pwdEt);

                dialog.setView(layout);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String pwd = pwdEt.getText().toString();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.modPwd(OperationActivity.this, Byte.valueOf(type), Byte.valueOf(id), pwd, new byte[]{00, 00, 00, 00, 00, 00}, new byte[]{00, 00, 00, 00, 00, 00});
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_del_pwd: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("删除密码:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

                dialog.setView(layout);


                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.delPwd(OperationActivity.this, Byte.valueOf(type), Byte.valueOf(id));
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_add_card: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("添加卡片:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                dialog.setView(layout);


                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.addCard(OperationActivity.this, Byte.valueOf(type), "00000001");
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_mod_card: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("修改卡片:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

                dialog.setView(layout);


                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.modCard(OperationActivity.this, Byte.valueOf(type), Byte.valueOf(id));
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_del_card: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("删除卡片:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

                dialog.setView(layout);


                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.delCard(OperationActivity.this, Byte.valueOf(type), Byte.valueOf(id));
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }


            case R.id.btn_add_fp: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("添加指纹:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                dialog.setView(layout);


                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.addFingerprint(OperationActivity.this, Byte.valueOf(type), "00000001");
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_mod_fp: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("修改指纹:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

                dialog.setView(layout);

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.modFingerprint(OperationActivity.this, Byte.valueOf(type), Byte.valueOf(id));
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_del_fp: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("删除指纹:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText typeET = new EditText(this);
                typeET.setText("0");
                layout.addView(typeET);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

                dialog.setView(layout);


                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.delFingerprint(OperationActivity.this, Byte.valueOf(type), Byte.valueOf(id));
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_wake_up: {
                byte[] bytes = LockBLEOpCmd.wakeup(OperationActivity.this);
                op(bytes);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
