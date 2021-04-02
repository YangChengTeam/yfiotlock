package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ShareDeviceWrapper;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/31
 **/
public class ShareDeviceEngine extends HttpCoreEngin {
    public ShareDeviceEngine(Context context) {
        super(context);
    }

    public static final String SHARE_DEVICE_SUCCESS = "share_device_success";

    public Observable<ResultInfo<String>> shareDevice(String receiveUid, String lockId) {
        Map<String, String> map = new HashMap<>();
        map.put("receive_uid", receiveUid);
        map.put("locker_id", lockId);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<List<ShareDeviceWrapper>>> getShareList(int p, String lockId) {
        Map<String, String> map = new HashMap<>();
        map.put("p", p + "");
        map.put("type", "1");
        map.put("locker_id", lockId);
        map.put("page_size", "10");
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<List<ShareDeviceWrapper>>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_LIST_URL, new TypeReference<ResultInfo<List<ShareDeviceWrapper>>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<List<ShareDeviceWrapper>>> getReceiveList(int p) {
        Map<String, String> map = new HashMap<>();
        map.put("p", p + "");
        map.put("type", "2");
        map.put("locker_id", "");
        map.put("page_size", "10");
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<List<ShareDeviceWrapper>>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_LIST_URL, new TypeReference<ResultInfo<List<ShareDeviceWrapper>>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param type   1:共享删除  2:接受删除
     * @param lockId 共享设备id
     * @return result
     */
    public Observable<ResultInfo<String>> deleteShare(int type, String lockId) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "" + type);
        map.put("id", lockId);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_DELETE_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<String>> receiveShare(String lockId) {
        Map<String, String> map = new HashMap<>();
        map.put("id", lockId);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_RECEIVE_URL, new TypeReference<ResultInfo<String>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<List<ShareDeviceWrapper>>> hasShare() {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<List<ShareDeviceWrapper>>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_HAS_URL, new TypeReference<ResultInfo<List<ShareDeviceWrapper>>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<DeviceInfo>> checkLockExist(String lockId) {
        Map<String, String> map = new HashMap<>();
        map.put("id", lockId);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<DeviceInfo>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_DEVICE_EXIST_URL, new TypeReference<ResultInfo<DeviceInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<List<ShareDeviceWrapper>>> getAllDevice(int p) {
        Map<String, String> map = new HashMap<>();
        map.put("p", "" + p);
        map.put("page_size", "10");
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<List<ShareDeviceWrapper>>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.SHARE_ALL_DEVICE_LIST_URL, new TypeReference<ResultInfo<List<ShareDeviceWrapper>>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
