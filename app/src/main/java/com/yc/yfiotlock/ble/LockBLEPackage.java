package com.yc.yfiotlock.ble;

import android.text.TextUtils;

import com.kk.utils.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class LockBLEPackage {

    // 最大传送单元
    private static int mtu = 512;

    private byte start;    //  包头
    private byte pid;      //  包标识
    private short length;  //  数据包长度
    private byte[] data;   //  数据区
    private short crc16;   //  校验位
    private byte end;      //  包尾

    public void setPid(byte pid) {
        this.pid = pid;
    }

    // START = 1 PID = 1 LENGTH = 2 CRC16 = 2 END = 1  total = 7
    private int nodataLen = 7;  //非数据区长度

    public static int getMtu() {
        return mtu;
    }

    public static void setMtu(int mtu) {
        LockBLEPackage.mtu = mtu;
    }

    public LockBLEPackage() {
        this.start = (byte) 0xAA;
        this.end = (byte) 0xBB;
        this.pid = 0x00;
    }

    public byte[] build(String key, LockBLEData bleData) {
        data = bleData.build(key);
        int len = data.length;

        // PID = 1 LENGTH = 2
        int precrcLen = 1 + 2;

        // START = 1 CRC16 = 2 END = 1
        length = (short) (1 + precrcLen + len + 2 + 1);

        // make crc16 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocate(len + precrcLen).order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = byteBuffer.put(pid)
                .putShort(length)
                .put(data).array();

        crc16 = (short) LockBLEUtil.crc16(bytes);

        ByteBuffer packageBuffer = ByteBuffer.allocate(len + nodataLen).put(start).put(bytes).putShort(crc16).put(end).order(ByteOrder.BIG_ENDIAN);

        return packageBuffer.array();
    }

    public byte[] build2(String key, LockBLEData bleData) {
        data = bleData.build(key);

        int maxdataLen = (mtu - nodataLen);
        // 分包操作 目前数据量应该都是1个包
        int packageCount = data.length / maxdataLen + ((data.length % maxdataLen) > 0 ? 1 : 0);
        int totalLen = 0;
        for (int i = 0; i < packageCount; i++) {
            int len = mtu;
            if (data.length < (i + 1) * maxdataLen) {
                len = nodataLen + (data.length - i * maxdataLen);
            }
            totalLen += len;
        }
        ByteBuffer packagesBuffer = ByteBuffer.allocate(totalLen).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < packageCount; i++) {
            pid = (byte) (packageCount - i - 1);

            int len = maxdataLen;
            if (data.length < (i + 1) * maxdataLen) {
                // data = N
                len = data.length - i * maxdataLen;
            }

            // PID = 1 LENGTH = 2
            int precrcLen = 1 + 2;

            // START = 1 CRC16 = 2 END = 1
            length = (short) (1 + precrcLen + len + 2 + 1);

            // make crc16 bytes
            ByteBuffer byteBuffer = ByteBuffer.allocate(len + precrcLen).order(ByteOrder.BIG_ENDIAN);
            byte[] bytes = byteBuffer.put(pid)
                    .putShort(length)
                    .put(Arrays.copyOfRange(data, i * maxdataLen, i * maxdataLen + len)).array();

            crc16 = (short) LockBLEUtil.crc16(bytes);

            ByteBuffer packageBuffer = ByteBuffer.allocate(len + nodataLen).order(ByteOrder.BIG_ENDIAN);
            packagesBuffer.put(packageBuffer.put(start)
                    .put(bytes)
                    .putShort(crc16)
                    .put(end).array());
        }

        return packagesBuffer.array();
    }


    // 解析响应数据  依照协议
    public static LockBLEData getData(byte[] response) {
        return getData(response, "");
    }

    public static LockBLEData getData(byte[] response, String key) {
        if (response == null) {
            LogUtil.msg("LockBLEPackage-> response is null!");
            return null;
        }

        // position START = 0  END = response.length - 1
        if (response[0] != (byte) 0xAA || response[response.length - 1] != (byte) 0xBB) {
            LogUtil.msg("LockBLEPackage-> START is not 0xAA or END is not 0xBB!");
            return null;
        }

        // position package LENGTH = 2,3
        short len = ByteBuffer.wrap(new byte[]{response[2], response[3]}).order(ByteOrder.BIG_ENDIAN).getShort();
        if (len != response.length) {
            LogUtil.msg("LockBLEPackage-> LENGTH is not package length!");
            return null;
        }

        // position package crc16_data = 1, response.length - 4
        short crc16 = (short) LockBLEUtil.crc16(Arrays.copyOfRange(response, 1, response.length - 3));

        // position package CRC16 = response.length - 3, response.length - 2
        if (crc16 != ByteBuffer.wrap(new byte[]{response[response.length - 3], response[response.length - 2]}).order(ByteOrder.BIG_ENDIAN).getShort()) {
            LogUtil.msg("LockBLEPackage-> package CRC16 is error!");
            return null;
        }

        byte[] data = Arrays.copyOfRange(response, 4, response.length - 3);


        if (!TextUtils.isEmpty(key)) {
            data = LockBLEUtil.decrypt(key, data);
            if (data.length == 0) {
                LogUtil.msg("LockBLEPackage-> decrypt error!");
                return null;
            }
        }

        // position data LENGTH = 0, 1
        len = ByteBuffer.wrap(new byte[]{data[0], data[1]}).getShort();
        if (len != response.length - 7) {
            LogUtil.msg("LockBLEPackage-> LENGTH is not data length!");
            return null;
        }

        // position data crc16_data = 4, response.length - 5
        crc16 = (short) LockBLEUtil.crc16(Arrays.copyOfRange(data, 0, data.length - 2));

        // position package CRC16 = response.length - 5, response.length - 4
        if (crc16 != ByteBuffer.wrap(new byte[]{data[data.length - 2], data[data.length - 1]}).order(ByteOrder.BIG_ENDIAN).getShort()) {
            LogUtil.msg("LockBLEPackage->data CRC16 is error!");
            return null;
        }

        LockBLEData lockBLEData = new LockBLEData();
        lockBLEData.setMcmd(data[6]);
        lockBLEData.setScmd(data[7]);
        lockBLEData.setStatus(data[8]);
        // LENGHT = 2 SEQ = 4 MCMD = 1 SCMD = 1 DATA STATUS = 1 CRC16 = 2
        if (len - 11 > 0) {
            lockBLEData.setExtra(Arrays.copyOfRange(data, 9, len - 2));
        }
        return lockBLEData;
    }
}
