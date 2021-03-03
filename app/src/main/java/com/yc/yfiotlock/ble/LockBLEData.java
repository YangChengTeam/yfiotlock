package com.yc.yfiotlock.ble;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LockBLEData {
    public static boolean isAesData = false;

    private byte mcmd;
    private byte scmd;
    private String body;

    public void setMcmd(byte mcmd) {
        this.mcmd = mcmd;
    }

    public void setScmd(byte scmd) {
        this.scmd = scmd;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] build(Context context) {

        byte[] data = null;
        short length = 0;
        if (body != null && body.length() > 0) {
            data = body.getBytes();
            length += data.length;
        }

        int seq = (int) (System.currentTimeMillis() / 1000);

        int precrcLen = 2 + 4 + 1 + 1;
        int len = precrcLen;
        byte[] bytes;
        if (data != null) {
            len += data.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(len).order(ByteOrder.LITTLE_ENDIAN);
            bytes = byteBuffer.putShort(length)
                    .putInt(seq)
                    .put(mcmd)
                    .put(scmd)
                    .put(data)
                    .array();
        } else {
            ByteBuffer byteBuffer = ByteBuffer.allocate(len).order(ByteOrder.LITTLE_ENDIAN);
            bytes = byteBuffer.putShort(length)
                    .putInt(seq)
                    .put(mcmd)
                    .put(scmd)
                    .array();
        }

        short crc16 = (short) LockBLEUtil.crc16(bytes, len);
        ByteBuffer packageBuffer = ByteBuffer.allocate(len + 2).order(ByteOrder.LITTLE_ENDIAN);
        byte[] dataBytes = packageBuffer
                .put(bytes)
                .putShort(crc16)
                .array();

        if (isAesData) {
            return LockBLEUtil.encode(context, dataBytes).getBytes();
        }

        return dataBytes;
    }


}
