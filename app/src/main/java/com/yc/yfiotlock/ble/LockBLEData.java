package com.yc.yfiotlock.ble;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LockBLEData {

    private byte mcmd;
    private byte scmd;
    private byte[] data;
    private byte status;
    private byte[] extra;
    public  boolean isEncrypt = false;

    public byte getMcmd() {
        return mcmd;
    }

    public byte getScmd() {
        return scmd;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setExtra(byte[] extra) {
        this.extra = extra;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public void setMcmd(byte mcmd) {
        this.mcmd = mcmd;
    }

    public void setScmd(byte scmd) {
        this.scmd = scmd;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    public byte[] build(String key) {
        short length = 0;
        if (this.data != null ) {
            length += data.length;
        }

        int seq = (int) (System.currentTimeMillis() / 1000);

        // LENGTH = 2 Seq = 4 MCMD = 1 SCMD = 1
        int precrcLen = 2 + 4 + 1 + 1;

        // CRC16 = 2
        length += precrcLen + 2;

        int len = precrcLen;
        byte[] bytes;
        if (data != null) {
            len += data.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(len).order(ByteOrder.BIG_ENDIAN);
            bytes = byteBuffer.putShort(length)
                    .putInt(seq)
                    .put(mcmd)
                    .put(scmd)
                    .put(data)
                    .array();
        } else {
            ByteBuffer byteBuffer = ByteBuffer.allocate(len).order(ByteOrder.BIG_ENDIAN);
            bytes = byteBuffer.putShort(length)
                    .putInt(seq)
                    .put(mcmd)
                    .put(scmd)
                    .array();
        }

        short crc16 = (short) LockBLEUtil.crc16(bytes);

        // CRC16 = 2
        ByteBuffer packageBuffer = ByteBuffer.allocate(len + 2).order(ByteOrder.BIG_ENDIAN);
        byte[] dataBytes = packageBuffer
                .put(bytes)
                .putShort(crc16)
                .array();

        if (isEncrypt()) {
            return LockBLEUtil.encrypt(key, dataBytes);
        }

        return dataBytes;
    }


}
