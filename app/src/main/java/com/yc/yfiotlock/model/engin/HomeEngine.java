package com.yc.yfiotlock.model.engin;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.HomeInfo;
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
public class HomeEngine extends BaseEngin {
    public HomeEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return null;
    }

    /**
     * @return list of families
     */
    public Observable<ResultInfo<List<HomeInfo>>> getHomeList() {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("user_id", UserInfoCache.getUserInfo().getId());
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        HttpCoreEngin<ResultInfo<List<HomeInfo>>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.HOME_LIST_URL, new TypeReference<ResultInfo<List<HomeInfo>>>() {
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
    public Observable<ResultInfo<String>> modifyAddress(String id, String name, String longitude,
                                                        String latitude, String address, @Nullable String detailAddress) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("id", id);
        map.put("name", name);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("address", address);
        map.put("detail_address", detailAddress == null ? "" : detailAddress);
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.HOME_INFO_MODIFY_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param id            home id
     * @param name          home name
     * @param longitude     -
     * @param latitude      -
     * @param address       -
     * @param detailAddress can be null
     * @return add success or not
     * notice:  if param not changed should use original value
     */
    public Observable<ResultInfo<String>> addFamily(String id, String name, String longitude,
                                                    String latitude, String address, @Nullable String detailAddress) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        map.put("id", id);
        map.put("name", name);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("address", address);
        map.put("detail_address", detailAddress == null ? "" : detailAddress);
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.HOME_INFO_ADD_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param familyId home id
     * @return set success or not
     */
    public Observable<ResultInfo<String>> setDefaultFamily(String familyId) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
            map.put("user_id", UserInfoCache.getUserInfo().getId());
        }
        map.put("family_id", familyId);
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.HOME_SET_DEFAULT_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

    /**
     * @param familyId home id
     * @return delete success or not
     */
    public Observable<ResultInfo<String>> deleteFamily(String familyId) {
        Map<String, String> map = new HashMap<>();
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
            map.put("user_id", UserInfoCache.getUserInfo().getId());
        }
        map.put("family_id", familyId);
        HttpCoreEngin<ResultInfo<String>> httpCoreEngin = new HttpCoreEngin<>(getContext());
        return httpCoreEngin.rxpost(Config.HOME_INFO_DELETE_URL, new TypeReference<ResultInfo<String>>() {
        }.getType(), map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }

}
