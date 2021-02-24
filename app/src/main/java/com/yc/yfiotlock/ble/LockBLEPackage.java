package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class LockBLEPackage {
    private byte start;
    private byte pid;
    private short length;  // 数据区加密之后的长度
    private byte[] data;
    private short crc16;
    private byte end;

    private static int mtu = 20;
    private int nodataLen = 7;  // 非数据区长度

    public static void setMtu(int mtu) {
        LockBLEPackage.mtu = mtu;
    }

    public LockBLEPackage() {
        this.start = (byte) 0xAA;
        this.end = (byte) 0xBB;
        this.pid = 0x00;
    }

    public byte[] build(Context context, LockBLEData bleData) {
        data = bleData.build(context);

        int maxDataLen = (mtu - nodataLen);
        int packageCount = data.length / maxDataLen + ((data.length % maxDataLen) > 0 ? 1 : 0);
        int totalLen = 0;
        for (int i = 0; i < packageCount; i++) {
            int len = mtu;
            if (data.length < (i + 1) * maxDataLen) {
                len = nodataLen + (data.length - i * maxDataLen);
            }
            totalLen += len;
        }
        ByteBuffer packagesBuffer = ByteBuffer.allocate(totalLen).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < packageCount; i++) {
            pid = (byte) (packageCount - i - 1);
            int len = maxDataLen;
            if (data.length < (i + 1) * maxDataLen) {
                len = data.length - i * maxDataLen;
            }

            int precrcLen = 1 + 2;
            length = (short) (len + precrcLen);
            ByteBuffer byteBuffer = ByteBuffer.allocate(len + precrcLen).order(ByteOrder.LITTLE_ENDIAN);
            byte[] bytes = byteBuffer.put(pid)
                    .putShort(length)
                    .put(Arrays.copyOfRange(data, i * maxDataLen, i * maxDataLen + len)).array();
            crc16 = (short) LockBLEUtil.crc16(bytes, len);

            ByteBuffer packageBuffer = ByteBuffer.allocate(len + nodataLen).order(ByteOrder.LITTLE_ENDIAN);
            packagesBuffer.put(packageBuffer.put(start)
                    .put(bytes)
                    .putShort(crc16)
                    .put(end).array());
        }
        return packagesBuffer.array();
    }
}
