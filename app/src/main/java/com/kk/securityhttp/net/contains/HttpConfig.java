package com.kk.securityhttp.net.contains;

import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.utils.OKHttpUtil;

import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by zhangkai on 16/9/9.
 */
public class HttpConfig {
    public static final MediaType MEDIA_TYPE = MediaType.parse("text/html");

    public static final int TIMEOUT = 20 * 1000;

    public static final int STATUS_OK = 1;
    public static final int SERVICE_ERROR_CODE = -110;

    public static final String NET_ERROR = "网络不给力,请稍后再试";

    public static void setDefaultParams(Map<String, String> params) {
        OKHttpUtil.setDefaultParams(params);
    }

    public static Map<String, String> getDefaultParams() {
        return OKHttpUtil.getDefaultParams();
    }

    public static void setPublickey(String publickey) {
        GoagalInfo.get().publicKey = publickey;
    }
}
