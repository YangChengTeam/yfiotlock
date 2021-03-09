package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;


public class LockBLESettingCmd {
    // 1.系统设置类(0x01)
    public static byte[] setting(Context context, byte scmd, String body) {
        LockBLEPackage lockBLEPackage = new LockBLEPackage();
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd((byte) 0x01);
        lockBLEData.setScmd(scmd);
        lockBLEData.setBody(body);
        return lockBLEPackage.build(context, lockBLEData);
    }

    // 1.1恢复出厂设置(0x01)
    public static byte[] reset(Context context) {
        return setting(context, (byte) 0x01, new String(new byte[]{0x01}));
    }

    // 1.2WIFI配网(0x02)
    public static byte[] wiftDistributionNetwork(Context context, String ssid, String pwd) {
        final byte[] ssidBuffer = new byte[32];
        System.arraycopy(ssid.getBytes(), 0, ssidBuffer, 0, ssid.length());
        final byte[] pwdBuffer = new byte[24];
        System.arraycopy(pwd.getBytes(), 0, pwdBuffer, 0, pwd.length());

        ByteBuffer bodyBuffer = ByteBuffer.allocate(ssidBuffer.length + pwdBuffer.length).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(ssidBuffer).put(pwdBuffer).array();
        return setting(context, (byte) 0x02, new String(bytes));
    }

    // 1.3绑定蓝牙(0x03)
    public static byte[] bindBle(Context context) {
        return setting(context, (byte) 0x03, null);
    }

    // 1.4验证身份(0x04)
    public static byte[] verifyIdentidy(Context context) {
        return setting(context, (byte) 0x04, null);
    }

    private static int getYearSuff(int year, int n) {
        if (n < 100) return year;
        return getYearSuff(year - (year / n) * n, n / 10);
    }

    // 1.5同步时间(0x05)
    public static byte[] syncTime(Context context) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        year = getYearSuff(year, 1000);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int date = Calendar.getInstance().get(Calendar.DATE);
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        return setting(context, (byte) 0x05, new String(new byte[]{(byte) year, (byte) month, (byte) date, (byte) hour, (byte) minute, (byte) second}));
    }

    // 1.6设置AES密钥(0x06)
    public static byte[] setAesKey(Context context, String key, String origkey) {
        return setting(context, (byte) 0x06, key + origkey);
    }

    // 1.7取消配网(0x07)
    public static byte[] cancelWifi(Context context) {
        return setting(context, (byte) 0x07, new String(new byte[]{((byte) 0x01)}));
    }

    // 1.8修改音量(0x08)
    public static byte[] changeVolume(Context context, int volume) {
        return setting(context, (byte) 0x08, new String(new byte[]{(byte) volume}));
    }

    // 1.7解绑蓝牙(0x09)
    public static byte[] cancelBle(Context context) {
        return setting(context, (byte) 0x09, new String(new byte[]{((byte) 0x01)}));
    }
}
