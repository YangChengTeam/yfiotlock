package com.yc.yfiotlock.model.engin;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
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

    public Observable<ResultInfo<DeviceInfo>> addDeviceInfo(String familyId, String name, String mac, String deviceId) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("family_id", familyId);
        map.put("name", name);
        map.put("device_id", deviceId);
        map.put("mac_address",  mac);
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

    public Observable<ResultInfo<String>> updateDeviceInfo(String id, String name) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", id);
        map.put("name", name);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.DEVICE_MODIFY_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
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
}
