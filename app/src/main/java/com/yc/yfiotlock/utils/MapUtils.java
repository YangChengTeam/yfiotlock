package com.yc.yfiotlock.utils;

import com.baidu.mapapi.model.LatLng;

public class MapUtils {
    public static boolean isLatlngEqual(LatLng latLng0, LatLng latLng1) {
        if (latLng0.latitude == latLng1.latitude
                && latLng0.longitude == latLng1.longitude) {
            return true;
        }

        return false;
    }
}
