
package com.yc.yfiotlock.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEPackage;
import com.yc.yfiotlock.ble.LockBLEUtil;
import com.yc.yfiotlock.controller.activitys.lock.remote.VisitorManageActivity;
import com.yc.yfiotlock.demo.comm.ObserverManager;
import com.yc.yfiotlock.libs.fingerprintcompat.AonFingerChangeCallback;
import com.yc.yfiotlock.libs.fingerprintcompat.FingerManager;
import com.yc.yfiotlock.libs.fingerprintcompat.SimpleFingerCheckCallback;
import com.yc.yfiotlock.helper.PermissionHelper;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";

    PermissionHelper permissionHelper;

    DeviceAdapter mDeviceAdapter;
    ListView listView;
    ProgressDialog progressDialog;
    FloatingActionButton fab;
    FloatingActionButton fab2;

    public static final int mtu = 512;

    private void initBle() {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 1000)
                .setSplitWriteNum(mtu)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000).init(getApplication());
    }

    private void initFingerprint() {
        switch (FingerManager.checkSupport(MainActivity.this)) {
            case DEVICE_UNSUPPORTED:
                showToast("您的设备不支持指纹");
                scan();
                break;
            case SUPPORT_WITHOUT_DATA:
                showToast("请在系统录入指纹后再验证");
                break;
            case SUPPORT:
                FingerManager.build().setApplication(getApplication())
                        .setTitle("指纹验证")
                        .setDes("请按下指纹")
                        .setNegativeText("取消")
                        .setFingerCheckCallback(new SimpleFingerCheckCallback() {

                            @Override
                            public void onSucceed() {
                                showToast("验证成功");
                                scan();
                            }

                            @Override
                            public void onError(String error) {
                                showToast("验证失败");
                            }

                            @Override
                            public void onCancel() {
                                showToast("您取消了识别");
                            }
                        })
                        .setFingerChangeCallback(new AonFingerChangeCallback() {

                            @Override
                            protected void onFingerDataChange() {
                                showToast("指纹数据发生了变化");
                            }
                        })
                        .create()
                        .startListener(MainActivity.this);
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initConfig() {
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)
                .setServiceUuids(new UUID[]{ UUID.fromString(SERVICE_UUID)})
                .setScanTimeOut(10000);

        EditText nameEt = findViewById(R.id.et_name);
        if (!TextUtils.isEmpty(nameEt.getText())) {
            builder.setDeviceName(true, nameEt.getText().toString());
        }
        BleManager.getInstance().initScanRule(builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        permissionHelper = new PermissionHelper();
        //initFingerprint();

        initBle();

        EditText nameEt = findViewById(R.id.et_name);
        nameEt.setImeActionLabel("搜索", KeyEvent.KEYCODE_ENTER);
        nameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    startScan();
                }
                return false;
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                startScan();
            }
        });



        listView = findViewById(R.id.rv_devices);
        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(final BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    Intent intent = new Intent(MainActivity.this, OperationActivity.class);
                    intent.putExtra("bleDevice", bleDevice);
                    startActivity(intent);
                }
            }
        });
        listView.setAdapter(mDeviceAdapter);

        if (!BleManager.getInstance().isBlueEnable()) {
            Toast.makeText(MainActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
            BleManager.getInstance().enableBluetooth();
            return;
        }

        scan();

    }


    private void scan() {
        permissionHelper.checkAndRequestPermission(MainActivity.this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !LockBLEUtil.checkGPSIsOpen(MainActivity.this)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("为了更精确的扫描到Bluetooth LE设备, 请打开GPS定位")
                            .setPositiveButton("确定", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, REQUEST_GPS);
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                    return;
                }
                startScan();
            }

            @Override
            public void onRequestPermissionError() {
                Toast.makeText(MainActivity.this, "授权失败, 无法扫描蓝牙设备", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void connect(final BleDevice bleDevice) {
        hideSoftKeyboard();
        try {
            progressDialog.setMessage("连接中...");
            progressDialog.show();
        } catch (Exception e) {
        }
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                if (!progressDialog.isShowing()) {
                    try {
                        progressDialog.setMessage("连接中...");
                        progressDialog.show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, bleDevice.getName() + getString(R.string.connect_fail) + exception.getDescription(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, bleDevice.getName() + "连接成功", Toast.LENGTH_LONG).show();

                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
                    @Override
                    public void onSetMTUFailure(BleException exception) {
                        Toast.makeText(MainActivity.this, "设置mtu失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMtuChanged(int mtu) {
                        // 设置MTU成功，并获得当前设备传输支持的MTU值
                        LockBLEPackage.setMtu(mtu);
                        Toast.makeText(MainActivity.this, "设置mtu成功" + mtu, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(MainActivity.this, bleDevice.getName() + getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, bleDevice.getName() + getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }


    private void startScan() {
        if (!BleManager.getInstance().isBlueEnable()) {
            Toast.makeText(MainActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
            BleManager.getInstance().enableBluetooth();
            return;
        }
        fab.hide();
        initConfig();
        hideSoftKeyboard();
        mDeviceAdapter.clearScanDevice();
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                try {
                    progressDialog.setMessage("扫描中...");
                    progressDialog.show();
                } catch (Exception e) {
                }

                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
                connect(bleDevice);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                progressDialog.dismiss();
                fab.show();
            }
        });
    }


    public static final int REQUEST_GPS = 4;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS) {
            if (LockBLEUtil.checkGPSIsOpen(this)) {
                startScan();
            }
        }
        permissionHelper.onRequestPermissionsResult(this, requestCode);
    }
}