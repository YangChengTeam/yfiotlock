package com.kk.securityhttp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String timestamp2Date(long timestamp) {
        if (timestamp < 2000000000) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);
    }
}
