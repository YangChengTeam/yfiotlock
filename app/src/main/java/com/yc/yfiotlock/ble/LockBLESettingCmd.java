package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


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
        return setting(context, (byte) 0x01, null);
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

    // 1.5同步时间(0x05)
    public static byte[] syncTime(Context context, int time) {
        return setting(context, (byte) 0x05, Integer.toBinaryString(time));
    }

    // 1.6设置AES密钥(0x06)
    public static byte[] setAesKey(Context context, String key) {
        return setting(context, (byte) 0x06, key);
    }

}
