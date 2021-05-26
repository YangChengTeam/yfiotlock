package com.yc.yfiotlock.ble;

import android.content.Context;

import com.kk.securityhttp.utils.LogUtil;

import java.nio.ByteBuffer;

public class LockBLEEventCmd extends LockBLEBaseCmd {

    public static final byte MCMD = (byte) 0x08;

    // 子命令（0x01）
    public static final byte SCMD_LOG = (byte) 0x01;

    // 无最新事件（0x00）
    public static final byte SCMD_NO_NEW_EVENT = (byte) 0x0A;
    // 指纹录入次数
    public static final byte SCMD_FINGERPRINT_INPUT_COUNT = (byte) 0x01;
    // 门铃（0x02）
    public static final byte SCMD_DOORBELL = (byte) 0x02;
    // 开门信息（0x03）
    public static final byte SCMD_OPEN_DOOR_INFO = (byte) 0x03;
    // 低电报警（0x04）
    public static final byte SCMD_LOW_BATTERY = (byte) 0x04;
    // 本地初始化（0x05）
    public static final byte SCMD_LOCAL_INIT = (byte) 0x05;
    // 门锁锁定（0x06）
    public static final byte SCMD_LOCK_CLOSED = (byte) 0x06;
    // 门未锁好（0x07） 预留目前版本无.
    public static final byte SCMD_LOCK_UNCLOSED = (byte) 0x07;
    // 门未关上（0x08） 预留目前版本无.
    public static final byte SCMD_DOOR_UNCLOSED = (byte) 0x08;
    // 防撬报警（0x09）
    public static final byte SCMD_AVOID_PRY_ALARM = (byte) 0x09;


    // 更新成功
    public static final byte SCMD_UPDATE_SUCCESS = (byte) 0x0F;

    // 2. 事件类 (0x08)
    public static byte[] op(String key, byte scmd, byte[] data) {
        LogUtil.msg("当前key:" + key);

        LockBLEPackage lockBLEPackage = new LockBLEPackage();
        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(MCMD);
        lockBLEData.setScmd(scmd);
        lockBLEData.setData(data);
        lockBLEData.setEncrypt(true);
        return lockBLEPackage.build(key, lockBLEData);
    }

    // 2.1 - 9
    public static byte[] event(String key, int id) {
        return op(key, SCMD_LOG, ByteBuffer.allocate(4).putInt(id).array());
    }
}
