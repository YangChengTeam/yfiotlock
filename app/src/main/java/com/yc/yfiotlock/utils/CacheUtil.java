package com.yc.yfiotlock.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.model.bean.user.UserInfo;

import java.lang.reflect.Type;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class CacheUtil {
    public static void setCache(String key, Object object) {
        String uid = "";
        UserInfo userInfo = UserInfoCache.getUserInfo();
        if (userInfo != null) {
            uid = userInfo.getId();
        }
        int sv = GoagalInfo.get().getPackageInfo().versionCode;
        MMKV.defaultMMKV().putString(uid + key + sv, JSON.toJSONString(object));
    }

    public static <T> T getCache(String key, Type type) {
        String uid = "";
        UserInfo userInfo = UserInfoCache.getUserInfo();
        if (userInfo != null) {
            uid = userInfo.getId();
        }
        int sv = GoagalInfo.get().getPackageInfo().versionCode;
        String jsonStr = MMKV.defaultMMKV().getString(uid + key + sv, "");
        try {
            return getResultInfo(jsonStr, type);
        } catch (Exception e) {
            LogUtil.msg(e.getMessage());
        }
        return null;
    }

    public static long getSendCodeTime(String key) {
        int sv = GoagalInfo.get().getPackageInfo().versionCode;
        return MMKV.defaultMMKV().getLong(key + sv, 0L);
    }

    public static void setSendCodeTime(String key, long time) {
        int sv = GoagalInfo.get().getPackageInfo().versionCode;
        MMKV.defaultMMKV().putLong(key + sv, time);
    }

    private static <T> T getResultInfo(String body, Type type) {
        T resultInfo = null;
        try {
            if (type != null) {
                resultInfo = JSON.parseObject(body, type);
            } else {
                resultInfo = JSON.parseObject(body, new TypeReference<T>() {
                }); //范型已被擦除 --！
            }
        } catch (Exception e) {
            //e.printStackTrace();
            LogUtil.msg("缓存解析不成功");
        }
        return resultInfo;
    }
}
