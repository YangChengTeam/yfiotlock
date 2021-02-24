package com.yc.yfiotlock.demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.demo.comm.ObserverManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OperationActivity extends AppCompatActivity implements View.OnClickListener {
    BleDevice bleDevice;
    BluetoothGattService service;
    BluetoothGattCharacteristic characteristic;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        bleDevice = ServiceActivity.getInstance().getBleDevice();
        service = CharacteristicActivity.getInstance().getService();
        characteristic = getIntent().getParcelableExtra("characteristic");
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

        TextView blenameTv = findViewById(R.id.tv_blename);
        blenameTv.setText("蓝牙名称:" + bleDevice.getName() + "\n服务uuid:" + service.getUuid() + "\n特征uuid:" + characteristic.getUuid());

        int charaProp = characteristic.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            BleManager.getInstance().read(
                    bleDevice,
                    service.getUuid().toString(),
                    characteristic.getUuid().toString(),
                    new BleReadCallback() {
                        @Override
                        public void onReadSuccess(byte[] data) {
                            Toast.makeText(OperationActivity.this, "Read成功:" + LockBLEUtil.toHexString(data), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onReadFailure(BleException exception) {
                            Toast.makeText(OperationActivity.this, "Read失败:" + exception.getDescription(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {

        }

        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {

        }

        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BleManager.getInstance().notify(
                    bleDevice,
                    service.getUuid().toString(),
                    characteristic.getUuid().toString(),
                    new BleNotifyCallback() {
                        @Override
                        public void onNotifySuccess() {
                            Toast.makeText(OperationActivity.this, "Notify成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNotifyFailure(BleException exception) {
                            Toast.makeText(OperationActivity.this, "Notify失败:" + exception.getDescription(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {
                            Toast.makeText(OperationActivity.this, "Notify响应:" + LockBLEUtil.toHexString(data), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BleManager.getInstance().indicate(
                    bleDevice,
                    service.getUuid().toString(),
                    characteristic.getUuid().toString(),
                    new BleIndicateCallback() {
                        @Override
                        public void onIndicateSuccess() {
                            Toast.makeText(OperationActivity.this, "Indicate成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onIndicateFailure(BleException exception) {
                            Toast.makeText(OperationActivity.this, "Indicate失败", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {
                            Toast.makeText(OperationActivity.this, "Indicate响应:" + LockBLEUtil.toHexString(data), Toast.LENGTH_LONG).show();
                        }
                    });
        }


        CheckBox checkBox = findViewById(R.id.ck_aes);
        checkBox.setChecked(LockBLEData.isAesData);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LockBLEData.isAesData = isChecked;
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(this);
        findViewById(R.id.btn_wifi).setOnClickListener(this);
        findViewById(R.id.btn_bind_ble).setOnClickListener(this);
        findViewById(R.id.btn_verid).setOnClickListener(this);
        findViewById(R.id.btn_synctime).setOnClickListener(this);
        findViewById(R.id.btn_setkey).setOnClickListener(this);

        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_add_pwd).setOnClickListener(this);
        findViewById(R.id.btn_mod_pwd).setOnClickListener(this);
        findViewById(R.id.btn_del_pwd).setOnClickListener(this);
        findViewById(R.id.btn_add_card).setOnClickListener(this);
        findViewById(R.id.btn_mod_card).setOnClickListener(this);
        findViewById(R.id.btn_del_card).setOnClickListener(this);
        findViewById(R.id.btn_add_fp).setOnClickListener(this);
        findViewById(R.id.btn_mod_fp).setOnClickListener(this);
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
                        service.getUuid().toString(),
                        characteristic.getUuid().toString(),
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
                ssidET.setHint("输入ssid");
                layout.addView(ssidET);

                final EditText pwdEt = new EditText(this);
                pwdEt.setHint("输入密码");
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("同步时间:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText timeET = new EditText(this);
                timeET.setText("2021-02-20 15:21:20");
                layout.addView(timeET);

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
                        String time = timeET.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(time);
                            byte[] bytes = LockBLESettingCmd.syncTime(OperationActivity.this, (int) (date.getTime() / 1000));
                            op(bytes);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_setkey: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("设置新密钥:");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText keyET = new EditText(this);
                keyET.setText("12345678");
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
                        byte[] bytes = LockBLESettingCmd.setAesKey(OperationActivity.this, key);
                        op(bytes);
                    }
                });

                dialog.show();
                break;
            }

            case R.id.btn_open: {
                byte[] bytes = LockBLEOpCmd.open(OperationActivity.this);
                op(bytes);
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
                        byte[] bytes = LockBLEOpCmd.addPwd(OperationActivity.this, Byte.valueOf(type), pwd);
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

                final EditText pwdEt = new EditText(this);
                pwdEt.setText("123456");
                layout.addView(pwdEt);

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

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
                        String pwd = pwdEt.getText().toString();
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.modPwd(OperationActivity.this, Byte.valueOf(id), Byte.valueOf(type), pwd);
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

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

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
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.delPwd(OperationActivity.this, Byte.valueOf(id), Byte.valueOf(type));
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
                        byte[] bytes = LockBLEOpCmd.addCard(OperationActivity.this, Byte.valueOf(type));
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

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

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
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.modCard(OperationActivity.this, Byte.valueOf(id), Byte.valueOf(type));
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

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

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
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.delCard(OperationActivity.this, Byte.valueOf(id), Byte.valueOf(type));
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
                        byte[] bytes = LockBLEOpCmd.addFingerprint(OperationActivity.this, Byte.valueOf(type));
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

                final EditText idET = new EditText(this);
                idET.setText("1");
                layout.addView(idET);

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
                        String id = idET.getText().toString();
                        String type = typeET.getText().toString();
                        byte[] bytes = LockBLEOpCmd.modFingerprint(OperationActivity.this, Byte.valueOf(id), Byte.valueOf(type));
                        op(bytes);
                    }
                });

                dialog.show();
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
