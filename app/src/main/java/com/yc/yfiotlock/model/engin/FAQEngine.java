package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.FAQInfo;
import com.yc.yfiotlock.utils.UserInfoCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/*
 * Created by　Dullyoung on 2021/3/6
 */
public class FAQEngine extends BaseEngin {
    public FAQEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.FQA_LIST_URL;
    }

    public Observable<ResultInfo<List<FAQInfo>>> getList(int p) {
        Map<String, String> map = new HashMap<>();
        map.put("page", p + "");
        map.put("page_size", "10");
        if (App.isLogin()) {
            map.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        return rxpost(new TypeReference<ResultInfo<List<FAQInfo>>>() {
                }.getType(),
                map, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
