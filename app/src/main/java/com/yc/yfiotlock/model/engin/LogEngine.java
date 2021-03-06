package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.LogListInfo;
import com.yc.yfiotlock.model.bean.WarnListInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class LogEngine extends BaseEngin {
    public LogEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return null;
    }

    public Observable<ResultInfo<LogListInfo>> getOpenLog(int lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("user_id", UserInfoCache.getUserInfo().getId());
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", String.valueOf(lockerId));
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        HttpCoreEngin<ResultInfo<LogListInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_OPEN_URL, new TypeReference<ResultInfo<LogListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<WarnListInfo>> getWarnLog(int lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("user_id", UserInfoCache.getUserInfo().getId());
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", String.valueOf(lockerId));
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        HttpCoreEngin<ResultInfo<WarnListInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_WARN_URL, new TypeReference<ResultInfo<WarnListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
