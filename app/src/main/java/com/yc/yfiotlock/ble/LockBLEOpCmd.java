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
        return op(context, (byte) 0x01, new String(new byte[]{(byte) 0x01}));
    }


    // 2.2添加密码(0x02)
    public static byte[] addPwd(Context context, byte type, String number, String pwd, byte[] startTime, byte[] endTime) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(pwd.length() + startTime.length + endTime.length + number.length() + 1).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(number.getBytes()).put(pwd.getBytes()).put(startTime).put(endTime).array();
        return op(context, (byte) 0x02, new String(bytes));
    }

    // 2.3修改密码(0x03)
    public static byte[] modPwd(Context context, byte type, byte id, String pwd, byte[] startTime, byte[] endTime) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(pwd.length() + startTime.length + endTime.length + 2).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(id).put(pwd.getBytes()).put(startTime).put(endTime).array();
        return op(context, (byte) 0x03, new String(bytes));
    }

    // 2.4删除管理员密码(0x04)
    public static byte[] delPwd(Context context, byte type, byte id) {
        return op(context, (byte) 0x04, new String(new byte[]{type, id}));
    }

    // 2.5添加管理员卡(0x05)
    public static byte[] addCard(Context context, byte type, String number) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(number.length() + 1).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(number.getBytes()).array();
        return op(context, (byte) 0x05, new String(bytes));
    }

    // 2.6修改卡(0x06)
    public static byte[] modCard(Context context, byte type, byte id) {
        return op(context, (byte) 0x06, new String(new byte[]{type, id}));
    }

    // 2.7删除卡(0x07)
    public static byte[] delCard(Context context, byte type, byte id) {
        return op(context, (byte) 0x07, new String(new byte[]{type, id}));
    }

    // 2.8添加管理员指纹(0x08)
    public static byte[] addFingerprint(Context context, byte type, String number) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(number.length() + 1).order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(number.getBytes()).array();
        return op(context, (byte) 0x08, new String(bytes));
    }

    // 2.9修改管理员指纹(0x09)
    public static byte[] modFingerprint(Context context, byte type, byte id) {
        return op(context, (byte) 0x09, new String(new byte[]{type, id}));
    }

    // 2.10删除指纹(0x0A)
    public static byte[] delFingerprint(Context context, byte type, byte id) {
        return op(context, (byte) 0x0A, new String(new byte[]{type, id}));
    }

    // 2.10唤醒门锁(0x0B)
    public static byte[] wakeup(Context context) {
        return op(context, (byte) 0x0B, new String(new byte[]{(byte) 0x01}));
    }

}
