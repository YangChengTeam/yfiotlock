package com.yc.yfiotlock.ble;

public class LockBLEBaseCmd {
    public static final byte STATUS_OK = (byte) 0x00;
    public static final byte STATUS_ERROR = (byte) 0x01;
    public static final byte STATUS_KEY_ERROR = (byte) 0x05;

    public static final byte STATUS_WRITE_ERROR = (byte) 0x10;
    public static final byte STATUS_NOTIFY_TIMEOUT_ERROR = (byte) 0x11;
    public static final byte STATUS_WAKEUP_ERROR = (byte) 0x12;


    public static final byte EMPTY_BODY = (byte) 0x01;
}
