package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class LoginEngin extends HttpCoreEngin {

    public LoginEngin(Context context) {
        super(context);
    }

    public Observable<ResultInfo<UserInfo>> aliFastLogin(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return rxpost(Config.ALI_FAST_LOGIN, new TypeReference<ResultInfo<UserInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> sendSmsCode(String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        return rxpost(Config.LOGIN_SEND_CODE_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<UserInfo>> smsCodeLogin(String phone, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("code", code);
        return rxpost(Config.SMS_CODE_LOGIN_URL, new TypeReference<ResultInfo<UserInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<UserInfo>> validateLogin() {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        return rxpost(Config.VALIDATE_LOGIN_INFO_URL, new TypeReference<ResultInfo<UserInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


}
