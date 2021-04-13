package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LockBLEOpCmd extends LockBLEBaseCmd {

    public static final byte MCMD = (byte) 0x02;
    public static final byte SCMD_OPEN = (byte) 0x01;
    public static final byte SCMD_ADD_PWD = (byte) 0x02;
    public static final byte SCMD_MOD_PWD = (byte) 0x03;
    public static final byte SCMD_DEL_PWD = (byte) 0x04;
    public static final byte SCMD_ADD_CARD = (byte) 0x05;
    public static final byte SCMD_MOD_CARD = (byte) 0x06;
    public static final byte SCMD_DEL_CARD = (byte) 0x07;
    public static final byte SCMD_ADD_PRINTFINGER = (byte) 0x08;
    public static final byte SCMD_MOD_PRINTFINGER = (byte) 0x09;
    public static final byte SCMD_DEL_PRINTFINGER = (byte) 0x0A;
    public static final byte SCMD_WAKE_UP = (byte) 0x0B;

    // 2.操作指统类(0x02)
    public static byte[] op(Context context, byte scmd, byte[] data) {
        LockBLEPackage lockBLEPackage = new LockBLEPackage();
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(MCMD);
        lockBLEData.setScmd(scmd);
        lockBLEData.setData(data);
        return lockBLEPackage.build(context, lockBLEData);
    }

    // 2.1远程开门(0x01)
    public static byte[] open(Context context) {
        return op(context, SCMD_OPEN, new byte[]{EMPTY_BODY});
    }


    // 2.2添加密码(0x02)
    public static byte[] addPwd(Context context, byte type, String number, String pwd, byte[] startTime, byte[] endTime) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(pwd.length() + startTime.length + endTime.length + number.length() + 1).order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(number.getBytes()).put(pwd.getBytes()).put(startTime).put(endTime).array();
        return op(context, SCMD_ADD_PWD, bytes);
    }

    // 2.3修改密码(0x03)
    public static byte[] modPwd(Context context, byte type, byte id, String pwd, byte[] startTime, byte[] endTime) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(pwd.length() + startTime.length + endTime.length + 2).order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(id).put(pwd.getBytes()).put(startTime).put(endTime).array();
        return op(context, SCMD_MOD_PWD, bytes);
    }

    // 2.4删除管理员密码(0x04)
    public static byte[] delPwd(Context context, byte type, byte id) {
        return op(context, SCMD_DEL_PWD, new byte[]{type, id});
    }

    // 2.5添加管理员卡(0x05)
    public static byte[] addCard(Context context, byte type, String number) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(number.length() + 1).order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(number.getBytes()).array();
        return op(context, SCMD_ADD_CARD, bytes);
    }

    // 2.6修改卡(0x06)
    public static byte[] modCard(Context context, byte type, byte id) {
        return op(context, SCMD_MOD_CARD, new byte[]{type, id});
    }

    // 2.7删除卡(0x07)
    public static byte[] delCard(Context context, byte type, byte id) {
        return op(context, SCMD_DEL_CARD, new byte[]{type, id});
    }

    // 2.8添加管理员指纹(0x08)
    public static byte[] addFingerprint(Context context, byte type, String number) {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(number.length() + 1).order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = bodyBuffer.put(type).put(number.getBytes()).array();
        return op(context, SCMD_ADD_PRINTFINGER, bytes);
    }

    // 2.9修改管理员指纹(0x09)
    public static byte[] modFingerprint(Context context, byte type, byte id) {
        return op(context, SCMD_MOD_PRINTFINGER, new byte[]{type, id});
    }

    // 2.10删除指纹(0x0A)
    public static byte[] delFingerprint(Context context, byte type, byte id) {
        return op(context, SCMD_DEL_PRINTFINGER, new byte[]{type, id});
    }

    // 2.10唤醒门锁(0x0B)
    public static byte[] wakeup(Context context) {
        return op(context, SCMD_WAKE_UP, new byte[]{EMPTY_BODY});
    }

}
