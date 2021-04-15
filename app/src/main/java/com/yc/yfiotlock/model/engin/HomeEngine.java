package com.yc.yfiotlock.model.engin;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Dullyoung
 * <p>
 * Created by　Dullyoung on 2021/3/10
 */
public class HomeEngine extends HttpCoreEngin {
    public HomeEngine(Context context) {
        super(context);
    }


    /**
     * @return list of families
     */
    public Observable<ResultInfo<List<FamilyInfo>>> getHomeList() {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        return rxpost(Config.HOME_LIST_URL, new TypeReference<ResultInfo<List<FamilyInfo>>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param id            home id
     * @param name          home name
     * @param longitude     -
     * @param latitude      -
     * @param address       -
     * @param detailAddress can be null
     * @return modify success or not
     */
    public Observable<ResultInfo<String>> modifyFamily(int id, String name, Double longitude,
                                                       Double latitude, String address, @Nullable String detailAddress) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("id", String.valueOf(id));
        map.put("name", name);
        map.put("longitude", longitude.toString());
        map.put("latitude", latitude.toString());
        map.put("address", address);
        map.put("detail_address", detailAddress == null ? "" : detailAddress);
        return rxpost(Config.HOME_INFO_MODIFY_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param name          home name
     * @param longitude     -
     * @param latitude      -
     * @param address       -
     * @param detailAddress can be null
     * @return add success or not
     * notice:  if param not changed should use original value
     */
    public Observable<ResultInfo<String>> addFamily(String name, Double longitude,
                                                    Double latitude, String address, @Nullable String detailAddress) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("name", name);
        map.put("longitude", longitude.toString());
        map.put("latitude", latitude.toString());
        map.put("address", address);
        map.put("detail_address", detailAddress == null ? "" : detailAddress);
        return rxpost(Config.HOME_INFO_ADD_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param familyId home id
     * @return set success or not
     */
    public Observable<ResultInfo<String>> setDefaultFamily(int familyId) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("family_id", String.valueOf(familyId));
        return rxpost(Config.HOME_SET_DEFAULT_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param familyId home id
     * @return delete success or not
     */
    public Observable<ResultInfo<String>> deleteFamily(int familyId) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("family_id", String.valueOf(familyId));
        return rxpost(Config.HOME_INFO_DELETE_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

}
