package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/*
 * Created by　Dullyoung on 2021/3/9
 */
public class FeedBackEngine extends BaseEngin {

    public FeedBackEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.FEEDBACK_SUGGEST_URL;
    }

    public Observable<ResultInfo<String>> addInfo(String mobile, String content, String locker_id, String img) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("mobile", mobile);
        map.put("content", content);
        map.put("locker_id", locker_id);
        map.put("img", img);
        return rxpost(new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
