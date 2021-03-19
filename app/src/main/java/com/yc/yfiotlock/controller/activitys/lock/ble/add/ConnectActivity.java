package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.utils.CommonUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectActivity extends BaseBackActivity {
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;

    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_secret)
    ImageView mIvSecret;
    @BindView(R.id.stv_next)
    SuperTextView mStvNext;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @BindView(R.id.tv_name)
    TextView mTvName;

    private BleDevice bleDevice;
    private String TAG = "aaaa";

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect;
    }

    @Override
    protected void initVars() {
        super.initVars();
        bleDevice = getIntent().getParcelableExtra("bleDevice");
    }

    WifiManager mWifiManager;

    @Override
    protected void initViews() {
        super.initViews();
        setClick(mStvNext, () -> Connect2Activity.start(getContext(), bleDevice));
          backNavBar.setTitle(bleDevice.getName());
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
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
        if (isDestroyed()) {
            return;
        }
        List<ScanResult> results = mWifiManager.getScanResults();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            if (CommonUtil.is24GHz(results.get(i).frequency) && !strings.contains(results.get(i).SSID)) {
                strings.add(results.get(i).SSID);
            }
        }
        Log.i(TAG, "扫描结果：共" + results.size() + "个,2.4GWiFi："
                + strings.size() + "个：" + strings);
        int length = strings.size();
        showChooseList(strings.toArray(new String[length]));
    }

    private void scanFailure() {
        mLoadingDialog.dismiss();
        ToastCompat.show(getContext(), "扫描Wifi失败");
    }

    private void setInfo() {

    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mIvSecret, () -> CommonUtil.hiddenEditText(mEtPwd, mIvSecret));
        setClick(R.id.tv_name, () -> {
            mLoadingDialog.show("扫描中...");
            mLoadingDialog.setCanCancel(false);
            scanWifi();
        });
    }

    private void showChooseList(CharSequence[] strings) {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("可用网络列表(2.4G)")
                .setItems(strings, (DialogInterface.OnClickListener) (dialog, which) -> {
                    mTvName.setText(strings[which]);
                    dialog.dismiss();
                }).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setLayout(ScreenUtil.getWidth(getContext()) - 100, ScreenUtil.getHeight(getContext()) / 2);
        }
    }


}
