package com.yc.yfiotlock.model.engin;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.adapters.AboutAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class LockEngine extends BaseEngin {

    public LockEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return null;
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
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.OPEN_LOCK_ADD_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


    /**
     * @param lockerId -
     * @return finger count ,pwd count and nfc count
     */
    public Observable<ResultInfo<OpenLockCountInfo>> getOpenLockInfoCount(String lockerId) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        return new HttpCoreEngin<ResultInfo<OpenLockCountInfo>>(getContext()).rxpost(Config.OPEN_LOCK_LIST_URL,
                new TypeReference<ResultInfo<OpenLockCountInfo>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


    /**
     * @param lockerId -
     * @param pwdType  1:finger 2:psw 3:door-card
     * @return list of single openLockWay's info
     */
    public Observable<ResultInfo<List<OpenLockInfo>>> getOpenLockWayList(String lockerId, String pwdType) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("locker_id", lockerId);
        map.put("pwd_type", pwdType);
        return new HttpCoreEngin<ResultInfo<List<OpenLockInfo>>>(getContext()).rxpost(Config.OPEN_LOCK_SINGLE_TYPE_LIST_URL,
                new TypeReference<ResultInfo<List<OpenLockInfo>>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param id   pwd id
     * @param name new pws name
     * @return modify success or not
     */
    public Observable<ResultInfo<String>> modifyPwsName(String id, String name) {

        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("name", name);
        map.put("id", id);
        return new HttpCoreEngin<ResultInfo<String>>(getContext()).rxpost(Config.OPEN_LOCK_MODIFY_PSW_URL,
                new TypeReference<ResultInfo<String>>() {
                }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }


}
