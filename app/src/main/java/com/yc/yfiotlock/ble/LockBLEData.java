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
        // LENGTH = 2 Seq = 4 MCMD = 1 SCMD = 1
        int precrcLen = 2 + 4 + 1 + 1;

        // CRC16 = 2
        short length = (short) (precrcLen + 2);


        int len = precrcLen;

        byte[] bytes;
        int seq = (int) (System.currentTimeMillis() / 1000);

        if (data != null) {
            len += data.length;
            length += data.length;
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
        ByteBuffer dataBuffer = ByteBuffer.allocate(len + 2).order(ByteOrder.BIG_ENDIAN);
        byte[] dataBytes = dataBuffer
                .put(bytes)
                .putShort(crc16)
                .array();

        if (isEncrypt()) {
            return LockBLEUtil.encrypt(key, dataBytes);
        }

        return dataBytes;
    }


}
