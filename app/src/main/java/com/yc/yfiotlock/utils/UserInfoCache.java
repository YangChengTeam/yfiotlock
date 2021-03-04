package com.yc.yfiotlock.utils;

import com.alibaba.fastjson.JSON;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.model.bean.UserInfo;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class UserInfoCache {
    public static UserInfo getUserInfo() {
        String jsonStr = MMKV.defaultMMKV().getString("userInfo", "");
        UserInfo userInfo = null;
        try {
            userInfo = JSON.parseObject(jsonStr, UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        MMKV.defaultMMKV().putString("userInfo", JSON.toJSONString(userInfo));
    }
}
