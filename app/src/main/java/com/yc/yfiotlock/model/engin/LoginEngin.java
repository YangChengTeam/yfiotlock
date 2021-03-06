package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.UserInfo;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class LoginEngin extends BaseEngin {

    public LoginEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return null;
    }

    public Observable<ResultInfo<UserInfo>> aliFastLogin(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        HttpCoreEngin<ResultInfo<UserInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.ALI_FAST_LOGIN, new TypeReference<ResultInfo<UserInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> sendSmsCode(String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOGIN_SEND_CODE_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<UserInfo>> smsCodeLogin(String phone, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("code", code);
        HttpCoreEngin<ResultInfo<UserInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SMS_CODE_LOGIN_URL, new TypeReference<ResultInfo<UserInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


}
