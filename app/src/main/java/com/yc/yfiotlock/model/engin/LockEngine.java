package com.yc.yfiotlock.model.engin;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.remote.NetworkStateInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PasswordInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class LockEngine extends HttpCoreEngin {

    public LockEngine(Context context) {
        super(context);
    }

    /**
     * @param lockerId  device id
     * @param name      device name
     * @param keyId     key id,from device client
     * @param pwdType   1:finger 2:psw 3:door-card
     * @param groupType the psw belong which group 0:manager 1:common user 2: temporary user 3:劫持用户?
     * @param pwd       if {@param pwdType} is finger or door-card ,it can be null
     * @return add success or not
     */
    public Observable<ResultInfo<String>> addOpenLockWay(String lockerId, String name, String keyId,
                                                         int pwdType, String groupType, @Nullable String pwd) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        map.put("name", name);
        map.put("keyid", keyId);
        map.put("pwd_type", pwdType + "");
        map.put("group_type", groupType);
        map.put("pwd", pwd == null ? "" : pwd);
        return rxpost(Config.OPEN_LOCK_ADD_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


    /**
     * @param lockerId -
     * @return finger count ,pwd count and nfc count
     */
    public Observable<ResultInfo<OpenLockCountInfo>> getOpenLockInfoCount(String lockerId, String type) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        map.put("type", type);
        return rxpost(Config.OPEN_LOCK_LIST_URL,
                new TypeReference<ResultInfo<OpenLockCountInfo>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


    /**
     * @param lockerId -
     * @return list of single openLockWay's info
     */
    public Observable<ResultInfo<List<OpenLockInfo>>> getOpenLockTypeList(String lockerId, String type, String groupType) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        map.put("pwd_type", type);
        map.put("type", groupType);
        return rxpost(Config.OPEN_LOCK_SINGLE_TYPE_LIST_URL,
                new TypeReference<ResultInfo<List<OpenLockInfo>>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param id   pwd id
     * @param name new pws name
     * @return modify success or not
     */
    public Observable<ResultInfo<String>> modifyOpenLockName(String id, String name) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("name", name);
        map.put("id", id);
        return rxpost(Config.OPEN_LOCK_MODIFY_PSW_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


    // 删除开门方式
    public Observable<ResultInfo<String>> delOpenLockWay(String id) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("id", id);
        return rxpost(Config.OPEN_LOCK_DEL_PSW_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    // 删除开门方式 同步本地
    public Observable<ResultInfo<String>> delOpenLockWaySyncLocal(String lockid, String keyid, String groupType) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockid);
        map.put("keyid", keyid);
        map.put("group_type", groupType);
        return rxpost(Config.OPEN_LOCK_DEL_LOCAL_PSW_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    // 修改名称 同步本地
    public Observable<ResultInfo<String>> modifyOpenLockNameSyncLocal(String lockid, String keyid, String groupType, String name) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("name", name);
        map.put("locker_id", lockid);
        map.put("keyid", keyid);
        map.put("group_type", groupType);
        return rxpost(Config.OPEN_LOCK_MODIFY_LOCAL_PSW_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    // 远程开锁
    public Observable<ResultInfo<String>> longOpenLock(String lockerId) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        return rxpost(Config.OPEN_LOCK_LONG_OPEN_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    // 获取一条开门方式
    public Observable<ResultInfo<OpenLockInfo>> getLockOpenTypeInfo(String lockid, String keyid, String groupType, String type) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockid);
        map.put("keyid", keyid);
        map.put("pwd_type", type);
        map.put("group_type", groupType);
        return rxpost(Config.GET_OPEN_LOCK_INFO_URL,
                new TypeReference<ResultInfo<OpenLockInfo>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    // 临时密码列表
    public Observable<ResultInfo<List<PasswordInfo>>> temporaryPwdList(String lockerId, int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        return rxpost(Config.OPEN_LOCK_TEMPORARY_PWD_LIST_URL,
                new TypeReference<ResultInfo<List<PasswordInfo>>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    public Observable<ResultInfo<NetworkStateInfo>> checkNetWork(String lockId) {
        Map<String, String> map = new HashMap<>();
        map.put("locker_id", lockId);
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        return rxpost(Config.DEVICE_CHECK_NETWORK_URL, new TypeReference<ResultInfo<NetworkStateInfo>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
