package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/*
 * Created by　Dullyoung on 2021/3/9
 */
public class UserEngine extends BaseEngin {
    public UserEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String[] getUrls() {
        return new String[]{
                Config.USER_NAME_UPD_URL,
                Config.USER_FACE_UPD_URL
        };
    }

    public Observable<ResultInfo<String>> changeNickName(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("nickname", name);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
            map.put("user_id", UserInfoCache.getUserInfo().getId());
        }
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.USER_NAME_UPD_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> changeFace(String face) {
        Map<String, String> map = new HashMap<>();
        map.put("face", face);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
            map.put("user_id", UserInfoCache.getUserInfo().getId());
        }
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.USER_FACE_UPD_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
