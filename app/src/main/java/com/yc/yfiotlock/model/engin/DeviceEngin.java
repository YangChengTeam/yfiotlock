package com.yc.yfiotlock.model.engin;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.TimeInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.model.bean.user.UpgradeInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class DeviceEngin extends BaseEngin {
    public DeviceEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return null;
    }

    public Observable<ResultInfo<DeviceInfo>> addDeviceInfo(String familyId, String name, String mac, String deviceId, int isOnline) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("family_id", familyId);
        map.put("name", name);
        map.put("device_id", deviceId);
        map.put("mac_address", mac);
        map.put("is_online", isOnline + "");
        return new HttpCoreEngin<ResultInfo<DeviceInfo>>(getContext()).rxpost(Config.DEVICE_ADD_URL,
                new TypeReference<ResultInfo<DeviceInfo>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> getDeviceInfo(String id) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("id", id);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_DETAIL_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> updateDeviceInfo(String id, String name, String aliDevname) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", id);
        if (!TextUtils.isEmpty(aliDevname)) {
            map.put("device_id", aliDevname);
        }
        map.put("name", name);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_MODIFY_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> updateDeviceInfo2(String mac, String name, String aliDevname) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("mac_address", mac);
        if (!TextUtils.isEmpty(aliDevname)) {
            map.put("device_id", aliDevname);
        }
        map.put("name", name);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_MODIFY_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> updateDeviceInfo(String id, String name) {
        return updateDeviceInfo(id, name, "");
    }

    public Observable<ResultInfo<String>> setDeviceVolume(String id, int volume) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", id);
        map.put("volume", volume + "");
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_SET_VOLUME_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> delDeviceInfo(String id) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", id);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_DEL_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> delDeviceInfo2(String mac) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("mac_address", mac);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_DEL_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<TimeInfo>> getTime() {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        return new HttpCoreEngin<ResultInfo<TimeInfo>>(getContext()).rxpost(Config.DEVICE_TIME_URL,
                new TypeReference<ResultInfo<TimeInfo>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<List<String>>> getMacList() {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        return new HttpCoreEngin<ResultInfo<List<String>>>(getContext()).rxpost(Config.DEVICE_LIST_URL,
                new TypeReference<ResultInfo<List<String>>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<UpdateInfo>> getUpdateInfo(String version) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("version", version);
        return new HttpCoreEngin<ResultInfo<UpdateInfo>>(getContext()).rxpost(Config.DEVICE_UPDATE_URL, new TypeReference<ResultInfo<UpdateInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
