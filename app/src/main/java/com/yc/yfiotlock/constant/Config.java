package com.yc.yfiotlock.constant;

public class Config {
    public static boolean DEBUG = false;
    public static boolean RESQUEST_FLAG = true;

    private final static String baseUrl = "http://locker.yf5g.cn";
    private final static String debugBaseUrl = "http://box.wuhanup.com/api";

    public final static String ALI_PHONE_SDK_APPID = "RJhiL+tSBZC+ksN3xh92OmWBY3/FgQa/TRdm9hZXk1DEvQK/eVvmMV4SU+PB7JlgTsjPBW5eKU3ozxUP+haCQOOicXODI6B85fsO2QS15fHUDqX2QuvEN98UwRRyRRQ/+8r9Gdt3GEstcDL32Aku+R6MDd/Dn191sTrT6MigMmnGeTN7xkS5V4n6Vd4hiYw5ruK9stT2fmxuzB/A8DS0IJsEVXFrCReffB9QE+60xF06+mXqG5U8VL0eiHRfrpLl6pqChcWjjWlHBlCncJDBxScg9jnlbHzrNDPUWlP7RkmPDYeVdgef0Q==";

    public final static String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA5KaI8l7xplShIEB0Pwgm\n" +
            "MRX/3uGG9BDLPN6wbMmkkO7H1mIOXWB/Jdcl4/IMEuUDvUQyv3P+erJwZ1rvNsto\n" +
            "hXdhp2G7IqOzH6d3bj3Z6vBvsXP1ee1SgqUNrjX2dn02hMJ2Swt4ry3n3wEWusaW\n" +
            "mev4CSteSKGHhBn5j2Z5B+CBOqPzKPp2Hh23jnIH8LSbXmW0q85a851BPwmgGEan\n" +
            "5HBPq04QUjo6SQsW/7dLaaAXfUTYETe0HnpLaimcHl741ftGyrQvpkmqF93WiZZX\n" +
            "wlcDHSprf8yW0L0KA5jIwq7qBeu/H/H5vm6yVD5zvUIsD7htX0tIcXeMVAmMXFLX\n" +
            "35duvYDpTYgO+DsMgk2Q666j6OcEDVWNBDqGHc+uPvYzVF6wb3w3qbsqTnD0qb/p\n" +
            "WxpEdgK2BMVz+IPwdP6hDsDRc67LVftYqHJLKAfQt5T6uRImDizGzhhfIfJwGQxI\n" +
            "7TeJq0xWIwB+KDUbFPfTcq0RkaJ2C5cKIx08c7lYhrsPXbW+J/W4M5ZErbwcdj12\n" +
            "hrfV8TPx/RgpJcq82otrNthI3f4QdG4POUhdgSx4TvoGMTk6CnrJwALqkGl8OTfP\n" +
            "KojOucENSxcA4ERtBw4It8/X39Mk0aqa8/YBDSDDjb+gCu/Em4yYvrattNebBC1z\n" +
            "ulK9uJIXxVPi5tNd7KlwLRMCAwEAAQ==\n" +
            "-----END PUBLIC KEY-----\n";

    public final static String PRIVACY_POLICY = "https://www.baidu.com";
    public final static String USER_AGREEMENT = "https://www.baidu.com";

    public final static String ALI_FAST_LOGIN = getBaseUrl() + "/userapp/v1.User/oneClickLogin";


    public final static String LOGIN_SEND_CODE_URL = "https://www.baidu.com";

    public static final String INIT_URL = getBaseUrl() + "/v1/Index/init" + isRsa();

    public final static int CLICK_LIMIT = 500;

    public static String isRsa() {
        return "?rsa=" + (RESQUEST_FLAG ? 1 : 0);
    }

    public static String getBaseUrl() {
        return (DEBUG ? debugBaseUrl : baseUrl);
    }
}
