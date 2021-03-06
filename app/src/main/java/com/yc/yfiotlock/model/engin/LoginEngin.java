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
}
