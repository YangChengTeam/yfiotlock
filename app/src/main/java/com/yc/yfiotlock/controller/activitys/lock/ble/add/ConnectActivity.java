package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.utils.CommonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ConnectActivity extends BaseAddActivity {
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.et_ssid)
    EditText mEtSsid;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_secret)
    ImageView mIvSecret;
    @BindView(R.id.stv_next)
    SuperTextView mStvNext;

    private BleDevice bleDevice;
    private WifiManager mWifiManager;
    private boolean isNav2Connect2;

    private static WeakReference<ConnectActivity> mInstance;

    public static void finish2() {
        if (mInstance != null && mInstance.get() != null) {
            mInstance.get().finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect;
    }


    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
    }

    @Override
    protected void initViews() {
        mInstance = new WeakReference<ConnectActivity>(this);
        super.initViews();
        backNavBar.setTitle(bleDevice.getName());
        setInfo();
    }

    private void setInfo() {
        mEtSsid.setText(CommonUtil.getSsid(this));
    }

    private void nav2next() {
        String ssid = mEtSsid.getText().toString();
        String pwd = mEtPwd.getText().toString();
        if (TextUtils.isEmpty(ssid)) {
            ToastCompat.show(this, "wifi名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd) || pwd.length() < 8) {
            ToastCompat.show(this, "密码不能为空或小于8个字符");
            return;
        }
        isNav2Connect2 = true;
        Intent intent = new Intent(this, Connect2Activity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("ssid", ssid);
        intent.putExtra("pwd", pwd);
        startActivity(intent);
    }

    private void scanWifi() {
        getPermissionHelper().setMustPermissions2(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (!mWifiManager.isWifiEnabled() && !mWifiManager.setWifiEnabled(true)) {
                    mLoadingDialog.dismiss();
                    ToastCompat.show(getContext(), "请先打开Wifi开关");
                    return;
                }
                mWifiManager.startScan();
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.show(getContext(), "未获取到必须权限，无法扫描附近的WIFI");
            }
        });
    }


    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                // scan failure handling
                scanFailure();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanReceiver);
    }

    private void scanSuccess() {
        mLoadingDialog.dismiss();
        if (CommonUtil.isActivityDestory(this)) {
            return;
        }
        List<ScanResult> results = mWifiManager.getScanResults();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            if (CommonUtil.is24GHz(results.get(i).frequency) && !strings.contains(results.get(i).SSID)) {
                strings.add(results.get(i).SSID);
            }
        }
        int length = strings.size();
        showChooseList(strings.toArray(new String[length]));
    }

    private void scanFailure() {
        mLoadingDialog.dismiss();
        ToastCompat.show(getContext(), "扫描Wifi失败");
    }


    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mIvSecret, () -> CommonUtil.hiddenEditText(mEtPwd, mIvSecret));
        setClick(mStvNext, () -> {
            nav2next();
        });
        setClick(R.id.iv_scan_wifi, () -> {
            if (mLoadingDialog.isShowing()) return;
            mLoadingDialog.show("扫描中...");
            mLoadingDialog.setCanCancel(false);
            scanWifi();
        });
    }

    private AlertDialog alertDialog;

    private void showChooseList(CharSequence[] strings) {
        if (alertDialog != null && alertDialog.isShowing()) return;
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("可用网络列表(2.4G)")
                .setItems(strings, (DialogInterface.OnClickListener) (dialog, which) -> {
                    mEtSsid.setText(strings[which]);
                    dialog.dismiss();
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setOnDismissListener(dialog -> {
            mEtPwd.requestFocus();
            getWindow().setSoftInputMode(5);
        });
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setLayout(ScreenUtil.getWidth(getContext()) - 100, ScreenUtil.getHeight(getContext()) / 2);
        }
    }


}
