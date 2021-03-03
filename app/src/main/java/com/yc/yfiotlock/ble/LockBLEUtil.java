package com.yc.yfiotlock.ble;

import android.content.Context;
import android.location.LocationManager;

public class LockBLEUtil {
    static {
        System.loadLibrary("yfble");
    }

    private static String key = "12345678";

    public static void setKey(String key) {
        LockBLEUtil.key = key;
    }

    public static native int crc16(byte[] bytes, int length);

    public static native String encode(Object context, String key, byte[] bytes);

    public static native String decode(Object context, String key, byte[] bytes);

    public static String encode(Object context, byte[] bytes) {
        return encode(context, key, bytes);
    }

    public static String decode(Object context, byte[] bytes) {
        return decode(context, key, bytes);
    }

    public static String toHexString(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder("");
        if (byteArray == null || byteArray.length <= 0)
            return null;

        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                hv = "0" + hv;
            } else {
                hv = "" + hv;
            }
            hexString.append(hv + " ");
        }

        return hexString.toString();
    }

    public static boolean checkGPSIsOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

}
