package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.remote.LogListInfo;
import com.yc.yfiotlock.model.bean.lock.remote.WarnListInfo;
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

    public Observable<ResultInfo<LogListInfo>> getOpenLog(String lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", String.valueOf(lockerId));
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        HttpCoreEngin<ResultInfo<LogListInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_OPEN_URL, new TypeReference<ResultInfo<LogListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<WarnListInfo>> getWarnLog(String lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", String.valueOf(lockerId));
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        HttpCoreEngin<ResultInfo<WarnListInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_WARN_URL, new TypeReference<ResultInfo<WarnListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> addLog(int lockerId, int event_id, int keyid, int type, int groupType, int log_type, String time) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("log_type", String.valueOf(log_type));
        map.put("event_id", String.valueOf(event_id));
        map.put("kid", String.valueOf(keyid));
        map.put("locker_id", String.valueOf(lockerId));
        map.put("type", String.valueOf(type));
        map.put("group_type", String.valueOf(groupType));
        map.put("time", time);
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_LOCAL_ADD_URL, new TypeReference<ResultInfo<WarnListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<LogListInfo>> getLocalOpenLog(String lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("user_id", UserInfoCache.getUserInfo().getId());
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", String.valueOf(lockerId));
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        HttpCoreEngin<ResultInfo<LogListInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_LOCAL_OPEN_URL, new TypeReference<ResultInfo<LogListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<LogListInfo>> getLocalWarnLog(String lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", String.valueOf(lockerId));
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        HttpCoreEngin<ResultInfo<LogListInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.LOG_LOCAL_WARN_URL, new TypeReference<ResultInfo<LogListInfo>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
