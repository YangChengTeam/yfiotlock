package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LockBLEOpCmd {
    // 2.操作指统类(0x02)
    public static byte[] op(Context context, byte scmd, String body) {
        LockBLEPackage lockBLEPackage = new LockBLEPackage();
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd((byte) 0x02);
        lockBLEData.setScmd(scmd);
        lockBLEData.setBody(body);
        return lockBLEPackage.build(context, lockBLEData);
    }

    // 2.1远程开门(0x01)
    public static byte[] open(Context context) {
        return op(context, (byte) 0x01, null);
    }


    // 2.2添加密码(0x02)
    public static byte[] addPwd(Context context, byte type, String pwd) {
        final byte[] pwdBuffer = new byte[6];
        System.arraycopy(pwd.getBytes(), 0, pwdBuffer, 0, pwd.getBytes().length);

        ByteBuffer bodyBuffer = ByteBuffer.allocate(pwdBuffer.length + 1).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(pwdBuffer).put(type).array();

        return op(context, (byte) 0x02, new String(bytes));
    }

    // 2.3修改密码(0x03)
    public static byte[] modPwd(Context context, byte id, byte type, String pwd) {
        final byte[] pwdBuffer = new byte[6];
        System.arraycopy(pwd.getBytes(), 0, pwdBuffer, 0, pwd.getBytes().length);

        ByteBuffer bodyBuffer = ByteBuffer.allocate(pwdBuffer.length + 2).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(pwdBuffer).put(id).put(type).array();

        return op(context, (byte) 0x03, new String(bytes));
    }

    // 2.4删除管理员密码(0x04)
    public static byte[] delPwd(Context context, byte id, byte type) {
        return op(context, (byte) 0x04, new String(new byte[]{id, type}));
    }

    // 2.5添加管理员卡(0x05)
    public static byte[] addCard(Context context, byte type) {
        return op(context, (byte) 0x05, new String(new byte[]{type}));
    }

    // 2.6修改卡(0x06)
    public static byte[] modCard(Context context, byte id, byte type) {
        return op(context, (byte) 0x06, new String(new byte[]{id, type}));
    }

    // 2.7删除卡(0x07)
    public static byte[] delCard(Context context, byte id, byte type) {
        return op(context, (byte) 0x07, new String(new byte[]{id, type}));
    }

    // 2.8添加管理员指纹(0x08)
    public static byte[] addFingerprint(Context context, byte type) {
        return op(context, (byte) 0x08, new String(new byte[]{type}));
    }

    // 2.9修改管理员指纹(0x09)
    public static byte[] modFingerprint(Context context, byte id, byte type) {
        return op(context, (byte) 0x09, new String(new byte[]{id, type}));
    }

}
