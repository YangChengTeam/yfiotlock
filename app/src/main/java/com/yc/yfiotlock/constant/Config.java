package com.yc.yfiotlock.constant;

public class Config {
    public static boolean DEBUG = false;
    public static boolean RESQUEST_FLAG = true;

    private final static String baseUrl = "http://a.6ll.com";
    private final static String debugBaseUrl = "http://box.wuhanup.com/api";

    public final static String ALI_PHONE_SDK_APPID = "RJhiL+tSBZC+ksN3xh92OmWBY3/FgQa/TRdm9hZXk1DEvQK/eVvmMV4SU+PB7JlgTsjPBW5eKU3ozxUP+haCQOOicXODI6B85fsO2QS15fHUDqX2QuvEN98UwRRyRRQ/+8r9Gdt3GEstcDL32Aku+R6MDd/Dn191sTrT6MigMmnGeTN7xkS5V4n6Vd4hiYw5ruK9stT2fmxuzB/A8DS0IJsEVXFrCReffB9QE+60xF06+mXqG5U8VL0eiHRfrpLl6pqChcWjjWlHBlCncJDBxScg9jnlbHzrNDPUWlP7RkmPDYeVdgef0Q==";

    public final static String PRIVACY_POLICY = "https://www.baidu.com";
    public final static String USER_AGREEMENT = "https://www.baidu.com";

    public static final String INIT_URL = getBaseUrl() + "/v1/Index/init" + isRsa();

    public final static int CLICK_LIMIT = 500;

    public static String isRsa() {
        return "?rsa=" + (RESQUEST_FLAG ? 1 : 0);
    }

    public static String getBaseUrl() {
        return (DEBUG ? debugBaseUrl : baseUrl);
    }
}
