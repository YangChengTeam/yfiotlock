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

    public final static String PRIVACY_POLICY = getBaseUrl() + "/userapp/v1.api_file/privacy";
    public final static String USER_AGREEMENT = getBaseUrl() + "/userapp/v1.api_file/serviceAgreement";

    public final static int SMS_CODE_LENGTH = 6;

    //登录

    public final static String ALI_FAST_LOGIN = getBaseUrl() + "/userapp/v1.User/oneClickLogin" + isRsa();
    public final static String LOGIN_SEND_CODE_URL = getBaseUrl() + "/userapp/v1.Sms/send" + isRsa();
    public final static String SMS_CODE_LOGIN_URL = getBaseUrl() + "/userapp/v1.User/codeLogin" + isRsa();
    public final static String VALIDATE_LOGIN_INFO_URL = getBaseUrl() + "/userapp/v1.User/oneUserLogin" + isRsa();


    //首页
    public final static String INDEX_FAMILY_URL = getBaseUrl() + "/userapp/v1.Home/homeFamily" + isRsa();
    public final static String INDEX_LIST_DEVICE_LIST = getBaseUrl() + "/userapp/v1.Home/homeLocker" + isRsa();
    public final static String INDEX_DETAIL_URL = getBaseUrl() + "/userapp/v1.Home/homeDetails" + isRsa();

    //FAQ

    public static final String FQA_LIST_URL = getBaseUrl() + "/userapp/v1.faq/faqList" + isRsa();

    public static final String INIT_URL = getBaseUrl() + "/v1/Index/init" + isRsa();

    //更新

    public static final String UPDATE_URL = getBaseUrl() + "/userapp/v1.User/aboutUs" + isRsa();

    //上传图片

    public static final String UPLOAD_PIC_URL = getBaseUrl() + "/userapp/v1.User/uploads" + "?rea=5";

    //反馈 建议

    public static final String FEEDBACK_SUGGEST_URL = getBaseUrl() + "/userapp/v1.Feedback/feedbackAdd" + isRsa();

    //用户信息更改

    public static final String USER_NAME_UPD_URL = getBaseUrl() + "/userapp/v1.User/nicknameUpd" + isRsa();
    public static final String USER_FACE_UPD_URL = getBaseUrl() + "/userapp/v1.User/faceUpd" + isRsa();

    //家庭相关

    public static final String HOME_LIST_URL = getBaseUrl() + "/userapp/v1.family/familyList" + isRsa();
    public static final String HOME_INFO_MODIFY_URL = getBaseUrl() + "/userapp/v1.family/familyUpd" + isRsa();
    public static final String HOME_INFO_ADD_URL = getBaseUrl() + "/userapp/v1.family/familyAdd" + isRsa();
    public static final String HOME_SET_DEFAULT_URL = getBaseUrl() + "/userapp/v1.family/familyDefSet" + isRsa();
    public static final String HOME_INFO_DELETE_URL = getBaseUrl() + "/userapp/v1.family/familyDel" + isRsa();

    // 设备相关
    public static final String DEVICE_ADD_URL = getBaseUrl() + "/userapp/v1.Locker/lockerAdd" + isRsa();
    public static final String DEVICE_DETAIL_URL = getBaseUrl() + "/userapp/v1.Locker/lockerDetails" + isRsa();
    public static final String DEVICE_MODIFY_URL = getBaseUrl() + "/userapp/v1.Locker/lockerNameUpd" + isRsa();
    public static final String DEVICE_SET_VOLUME_URL = getBaseUrl() + "/userapp/v1.Locker/setVolume" + isRsa();
    public static final String DEVICE_DEL_URL = getBaseUrl() + "/userapp/v1.Locker/lockerDel" + isRsa();
    public static final String DEVICE_LIST_URL = getBaseUrl() + "/userapp/v1.Locker/lockerMacs" + isRsa();
    public static final String DEVICE_TIME_URL = getBaseUrl() + "/userapp/v1.User/getTime" + isRsa();
    public static final String DEVICE_UPDATE_URL = getBaseUrl() + "/userapp/v1.Index/upgrade" + isRsa();

    // 安全设备
    public static final String DEVICE_SET_SAFE_URL = getBaseUrl() + "/userapp/v1.Locker/setOperationPwd" + isRsa();

    //开锁管理相关
    public static final String OPEN_LOCK_ADD_URL = getBaseUrl() + "/userapp/v1.Locker/lockerPwdAdd" + isRsa();
    public static final String OPEN_LOCK_LIST_URL = getBaseUrl() + "/userapp/v1.Locker/lockerPwdList" + isRsa();
    public static final String OPEN_LOCK_SINGLE_TYPE_LIST_URL = getBaseUrl() + "/userapp/v1.Locker/pwdTypeList" + isRsa();
    public static final String OPEN_LOCK_MODIFY_PSW_URL = getBaseUrl() + "/userapp/v1.Locker/lockerPwdUpd" + isRsa();
    public static final String OPEN_LOCK_DEL_PSW_URL = getBaseUrl() + "/userapp/v1.Locker/lokerPwdDel" + isRsa();
    public static final String OPEN_LOCK_LONG_OPEN_URL = getBaseUrl() + "/userapp/v1.Locker/longRangeOPen" + isRsa();
    public static final String OPEN_LOCK_TEMPORARY_PWD_LIST_URL = getBaseUrl() + "/userapp/v1.Locker/temporaryPwdList" + isRsa();

    public static final String OPEN_LOCK_DEL_PSW_URL2 = getBaseUrl() + "/userapp/v1.Locker/lokerPwdDel2" + isRsa();
    public static final String OPEN_LOCK_MODIFY_PSW_URL2 = getBaseUrl() + "/userapp/v1.Locker/lockerPwdUpd2" + isRsa();

    //远程
    public static final String LOG_WARN_URL = getBaseUrl() + "/userapp/v1.locker_warn_log/warnLogList" + isRsa();
    public static final String LOG_OPEN_URL = getBaseUrl() + "/userapp/v1.locker_open_log/openLogList" + isRsa();

    public static final String LOG_LOCAL_WARN_URL = getBaseUrl() + "/userapp/v1.locker_local_log/warnLogList" + isRsa();
    public static final String LOG_LOCAL_OPEN_URL = getBaseUrl() + "/userapp/v1.locker_local_log/openLogList" + isRsa();
    public static final String LOG_LOCAL_ADD_URL = getBaseUrl() + "/userapp/v1.locker_local_log/logAdd" + isRsa();

    //分享设备相关
    public static final String GET_USER_INFO_URL = getBaseUrl() + "/userapp/v1.locker_share/getReceiveInfo" + isRsa();
    public static final String SHARE_DEVICE_URL = getBaseUrl() + "/userapp/v1.locker_share/lockerShareAdd" + isRsa();
    public static final String SHARE_DEVICE_LIST_URL = getBaseUrl() + "/userapp/v1.locker_share/lockerShareList" + isRsa();
    public static final String SHARE_ALL_DEVICE_LIST_URL = getBaseUrl() + "/userapp/v1.locker_share/lockerList" + isRsa();
    public static final String SHARE_DEVICE_DELETE_URL = getBaseUrl() + "/userapp/v1.locker_share/lockerShareDel" + isRsa();
    public static final String SHARE_DEVICE_RECEIVE_URL = getBaseUrl() + "/userapp/v1.locker_share/lockerShareAgree" + isRsa();
    public static final String SHARE_DEVICE_HAS_URL = getBaseUrl() + "/userapp/v1.locker_share/lockerShareHas" + isRsa();
    public static final String SHARE_DEVICE_EXIST_URL = getBaseUrl() + "/userapp/v1.locker/lockerHas" + isRsa();

    public static final String DEVICE_CHECK_NETWORK_URL = getBaseUrl() + "/userapp/v1.locker/lockerOnlineStatus" + isRsa();

    public static final int CLICK_LIMIT = 500;

    public static String isRsa() {
        return RESQUEST_FLAG ? "" : "?rea=5";
    }

    public static String getBaseUrl() {
        return (DEBUG ? debugBaseUrl : baseUrl);
    }
}
