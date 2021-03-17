package com.yc.yfiotlock.utils;

import com.yc.yfiotlock.ble.LockBLEManager;

public class BleUtil {
    public static int getType(String title) {
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
