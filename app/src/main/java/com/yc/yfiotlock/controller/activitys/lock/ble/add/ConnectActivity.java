package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
import com.kk.utils.ScreenUtil;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.eventbus.BleNotifyEvent;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ConnectActivity extends BaseConnectActivity {
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
    @BindView(R.id.tv_skip)
    TextView mStvSkip;

    private boolean showScanWifiResult = false;

    private WifiManager mWifiManager;
    private AlertDialog wifiAlertDialog;

    private static WeakReference<ConnectActivity> mInstance;
    public static void safeFinish() {
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
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerScanWifiReceiver();
    }

    private void registerScanWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
    }


    @Override
    protected void initViews() {
        super.initViews();
        mInstance = new WeakReference<>(this);
        backNavBar.setTitle(bleDevice.getName());
        setInfo();

        if ("".equals(mEtSsid.getText().toString())) {
            mEtSsid.requestFocus();
        } else {
            mEtPwd.requestFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mEtPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                nav2next();
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanReceiver);
    }

    private void setInfo() {
        if (isActiveDistributionNetwork) {
            mStvSkip.setVisibility(View.GONE);
        }
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
        Intent intent = new Intent(this, Connect2Activity.class);
        intent.putExtra("family", familyInfo);
        intent.putExtra("bleDevice", bleDevice);
        intent.putExtra("device", lockInfo);
        intent.putExtra("ssid", ssid);
        intent.putExtra("pwd", pwd);
        intent.putExtra("isActiveDistributionNetwork", isActiveDistributionNetwork);
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
                    showScanWifiResult = false;
                    ToastCompat.show(getContext(), "请先打开Wifi开关");
                    return;
                }
                mWifiManager.startScan();
                showScanWifiResult = true;
            }

            @Override
            public void onRequestPermissionError() {
                showScanWifiResult = false;
                ToastCompat.show(getContext(), "未获取到必须权限，无法扫描附近的WIFI");
            }
        });
    }

    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    };

    private void scanSuccess() {
        if (!showScanWifiResult) {
            return;
        }
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
        setClick(mStvNext, this::nav2next);
        setClick(R.id.iv_scan_wifi, () -> {
            if (mLoadingDialog.isShowing()) {
                return;
            }
            mLoadingDialog.show("扫描中...");
            mLoadingDialog.setCanCancel(false);
            scanWifi();
        });
        setClick(R.id.tv_skip, this::bleGetAliDeviceName);
    }


    private void showChooseList(CharSequence[] strings) {
        if (wifiAlertDialog == null) {
            wifiAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("可用网络列表(2.4G)")
                    .setItems(strings, (dialog, which) -> {
                        mEtSsid.setText(strings[which]);
                        dialog.dismiss();
                    }).create();
        }
        if (wifiAlertDialog.isShowing()) {
            return;
        }
        wifiAlertDialog.show();
        wifiAlertDialog.setOnDismissListener(dialog -> {
            mEtPwd.requestFocus();
            getWindow().setSoftInputMode(5);
        });
        Window window = wifiAlertDialog.getWindow();
        if (window != null) {
            window.setLayout(ScreenUtil.getWidth(getContext()) - 100, ScreenUtil.getHeight(getContext()) / 2);
        }
    }



}
