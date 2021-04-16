package com.yc.yfiotlock.utils;

import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
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
}
