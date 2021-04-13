package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Locale;


public class LockBLESettingCmd extends LockBLEBaseCmd {
    public static final byte MCMD = (byte) 0x01;
    public static final byte SCMD_RESET = (byte) 0x01;
    public static final byte SCMD_DISTRIBUTION_NETWORK = (byte) 0x02;
    public static final byte SCMD_BIND_BLE = (byte) 0x03;
    public static final byte SCMD_VERIFY_IDENTIDY = (byte) 0x04;
    public static final byte SCMD_SYNC_TIME = (byte) 0x05;
    public static final byte SCMD_SET_AES_KEY = (byte) 0x06;
    public static final byte SCMD_CANCEL_OP = (byte) 0x07;
    public static final byte SCMD_CHANGE_VOLUME = (byte) 0x08;
    public static final byte SCMD_UNBIND_BLE = (byte) 0x09;
    public static final byte SCMD_GET_ALIDEVICE_NAME = (byte) 0x0A;
    public static final byte SCMD_OPEN_UPDATE = (byte) 0x0B;
    public static final byte SCMD_UPDATE = (byte) 0x0C;
    public static final byte SCMD_GET_BATTERY = (byte) 0x0D;
    public static final byte SCMD_GET_VERSION = (byte) 0x0E;

    // 1.系统设置类(0x01)
    public static byte[] setting(Context context, byte scmd, byte[] data) {
        return setting(context, scmd, data, (byte) 0x00);
    }

    public static byte[] setting(Context context, byte scmd, byte[] data, byte pid) {
        LockBLEPackage lockBLEPackage = new LockBLEPackage();
        lockBLEPackage.setPid(pid);
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(MCMD);
        lockBLEData.setScmd(scmd);
        lockBLEData.setData(data);
        return lockBLEPackage.build(context, lockBLEData);
    }

    // 1.1恢复出厂设置(0x01)
    public static byte[] reset(Context context) {
        return setting(context, SCMD_RESET, new byte[]{EMPTY_BODY});
    }

    // 1.2WIFI配网(0x02)
    public static byte[] wiftDistributionNetwork(Context context, String ssid, String pwd) {
        final byte[] ssidBuffer = new byte[32];
        System.arraycopy(ssid.getBytes(), 0, ssidBuffer, 0, ssid.getBytes().length);
        final byte[] pwdBuffer = new byte[24];
        System.arraycopy(pwd.getBytes(), 0, pwdBuffer, 0, pwd.getBytes().length);

        ByteBuffer bodyBuffer = ByteBuffer.allocate(ssidBuffer.length + pwdBuffer.length).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(ssidBuffer).put(pwdBuffer).array();
        return setting(context, SCMD_DISTRIBUTION_NETWORK, bytes);
    }

    // 1.3绑定蓝牙(0x03)
    public static byte[] bindBle(Context context, String code) {
        return setting(context, SCMD_BIND_BLE, code.getBytes());
    }

    // 1.4验证身份(0x04)
    public static byte[] verifyIdentidy(Context context) {
        return setting(context, SCMD_VERIFY_IDENTIDY, null);
    }

    private static int getYearSuff(int year, int n) {
        if (n < 100) return year;
        return getYearSuff(year - (year / n) * n, n / 10);
    }

    // 1.5同步时间(0x05)
    public static byte[] syncTime(Context context, long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(timestamp * 1000L);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        year = getYearSuff(year, 1000);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        return setting(context, SCMD_SYNC_TIME, new byte[]{(byte) year, (byte) month, (byte) date, (byte) hour, (byte) minute, (byte) second});
    }

    // 1.6设置AES密钥(0x06)
    public static byte[] setAesKey(Context context, String key, String origkey) {
        return setting(context, SCMD_SET_AES_KEY, (key + origkey).getBytes());
    }

    // 1.7取消操作(0x07)
    public static byte[] cancelOp(Context context) {
        return setting(context, SCMD_CANCEL_OP, new byte[]{EMPTY_BODY});
    }

    // 1.8修改音量(0x08)
    public static byte[] changeVolume(Context context, int volume) {
        return setting(context, SCMD_CHANGE_VOLUME, new byte[]{(byte) volume});
    }

    // 1.9解绑蓝牙(0x09)
    public static byte[] unbindBle(Context context) {
        return setting(context, SCMD_UNBIND_BLE, new byte[]{EMPTY_BODY});
    }

    //  1.10获取门锁属性（0x0A）
    public static byte[] getAliDeviceName(Context context) {
        return setting(context, SCMD_GET_ALIDEVICE_NAME, new byte[]{EMPTY_BODY});
    }

    //  1.11开启升级（0x0B）
    public static byte[] openUpdate(Context context) {
        return setting(context, SCMD_OPEN_UPDATE, new byte[]{EMPTY_BODY});
    }

    //  1.12升级（0x0C）
    public static byte[] update(Context context, byte[] bytes) {
        return setting(context, SCMD_UPDATE, bytes);
    }

    //  1.13获取电量（0x0D）
    public static byte[] getBattery(Context context) {
        return setting(context, SCMD_GET_BATTERY, new byte[]{EMPTY_BODY});
    }

    //  1.14获取版本（0x0D）
    public static byte[] getVersion(Context context) {
        return setting(context, SCMD_GET_VERSION, new byte[]{EMPTY_BODY});
    }
}


