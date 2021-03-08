package com.yc.yfiotlock.constant;

public class Config {
    public static boolean DEBUG = false;
    public static boolean RESQUEST_FLAG = true;

    private final static String baseUrl = "http://locker.yf5g.cn";
    private final static String debugBaseUrl = "http://box.wuhanup.com/api";

    public final static String ALI_PHONE_SDK_APPID = "RJhiL+tSBZC+ksN3xh92OmWBY3/FgQa/TRdm9hZXk1DEvQK/eVvmMV4SU+PB7JlgTsjPBW5eKU3ozxUP+haCQOOicXODI6B85fsO2QS15fHUDqX2QuvEN98UwRRyRRQ/+8r9Gdt3GEstcDL32Aku+R6MDd/Dn191sTrT6MigMmnGeTN7xkS5V4n6Vd4hiYw5ruK9stT2fmxuzB/A8DS0IJsEVXFrCReffB9QE+60xF06+mXqG5U8VL0eiHRfrpLl6pqChcWjjWlHBlCncJDBxScg9jnlbHzrNDPUWlP7RkmPDYeVdgef0Q==";

    public final static String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsyzkcC39Rkv4XFPjTuEq\n" +
            "PH9shwXAtfhSv/bCZzlBOyp+YbEDhSDQAYRheEGoblDgpRTOODRb1zV9oQIjA6Eo\n" +
            "h6iLXNWl0uo/uLkGr02eyHgYG34UKa4eBFTIVHUIt68vowFFCp09iFpas6ktTENe\n" +
            "x9K5tZ6odh+A0LaUeA+OtPtmva2RpCcI/E5u4qDranXmfnxS921wZLnZWcv658AS\n" +
            "/42AyvT3d47qxwSU5icKWf5xjmrxc79tspOpbn30dgwuAVgaQLDSmxxH5Jux3Fcr\n" +
            "NLcSLdh57BYZIlsgFwieSF4cag6p1AAvGBW/80K0rTy7133jUiJpqGofMTPzvun2\n" +
            "ZwIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    public final static String PRIVACY_POLICY = "https://www.baidu.com";
    public final static String USER_AGREEMENT = "https://www.baidu.com";


    //登陆
    public final static String ALI_FAST_LOGIN = getBaseUrl() + "/userapp/v1.User/oneClickLogin" + isRsa();
    public final static String LOGIN_SEND_CODE_URL = getBaseUrl() + "/userapp/v1.Sms/send" + isRsa();
    public final static String SMS_CODE_LOGIN_URL = getBaseUrl() + "/userapp/v1.User/codeLogin" + isRsa();

    //FAQ
    public final static String FQA_LIST_URL = getBaseUrl() + "/userapp/v1.faq/faqList" + isRsa();

    public static final String INIT_URL = getBaseUrl() + "/v1/Index/init" + isRsa();

    public final static String UPDATE_URL = "http://a.6ll.com/v1/Index/aboutme" + isRsa();

    public final static int CLICK_LIMIT = 500;

    public static String isRsa() {
        return "?rsa=" + (RESQUEST_FLAG ? 1 : 0);
    }

    public static String getBaseUrl() {
        return (DEBUG ? debugBaseUrl : baseUrl);
    }
}
