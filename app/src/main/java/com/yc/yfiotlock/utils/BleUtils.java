package com.yc.yfiotlock.utils;

public class BleUtils {
    public static int getType(String title) {
        if (title.equals("指纹")) {
            return 1;
        } else if (title.equals("密码")) {
            return 2;
        } else if (title.equals("NFC门卡")) {
            return 3;
        }
        return 1;
    }
}
