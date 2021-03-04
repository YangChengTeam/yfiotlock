package com.yc.yfiotlock.constant;

public class Config {
    public static boolean DEBUG = false;
    public static boolean RESQUEST_FLAG = true;

    private final static String baseUrl = "http://a.6ll.com";
    private final static String debugBaseUrl = "http://box.wuhanup.com/api";

    public static final String INIT_URL = getBaseUrl() + "/v1/Index/init" + isRsa();

    public final static int CLICK_LIMIT = 500;

    public static String isRsa() {
        return "?rsa=" + (RESQUEST_FLAG ? 1 : 0);
    }

    public static String getBaseUrl() {
        return (DEBUG ? debugBaseUrl : baseUrl);
    }
}
