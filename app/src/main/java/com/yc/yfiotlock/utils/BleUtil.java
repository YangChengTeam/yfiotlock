package com.yc.yfiotlock.utils;

import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.yc.yfiotlock.App;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.IndexInfo;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BleUtil {
    public static int getType(String title) {
        if (title == null) return -1;
        if (title.equals("指纹")) {
            return LockBLEManager.OPEN_LOCK_FINGERPRINT;
        } else if (title.equals("密码")) {
            return LockBLEManager.OPEN_LOCK_PASSWORD;
        } else if (title.equals("NFC门卡")) {
            return LockBLEManager.OPEN_LOCK_CARD;
        }
        return LockBLEManager.OPEN_LOCK_CARD;
    }

    public static boolean isFoundDevice(@NonNull String mac) {
        List<String> macList = App.getApp().getMacList();
        if (macList != null) {
            for (String tmac : macList) {
                if (mac.equals(tmac)) {
                    return true;
                }
            }
        }
        IndexInfo indexInfo = CacheUtil.getCache(Config.INDEX_DETAIL_URL, IndexInfo.class);
        if (indexInfo != null && indexInfo.getDeviceInfos() != null && indexInfo.getDeviceInfos().size() > 0) {
            for (DeviceInfo deviceInfo : indexInfo.getDeviceInfos()) {
                if (mac.equals(deviceInfo.getMacAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkGPSIsOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
}
