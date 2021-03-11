package com.yc.yfiotlock.utils;

import com.yc.yfiotlock.constant.Config;

public class BleUtils {
    public static int getType(String title) {
        if (title.equals("指纹")) {
            return Config.OPEN_LOCK_FINGERPRINT;
        } else if (title.equals("密码")) {
            return Config.OPEN_LOCK_PASSWORD;
        } else if (title.equals("NFC门卡")) {
            return Config.OPEN_LOCK_CARD;
        }
        return Config.OPEN_LOCK_CARD;
    }
}
