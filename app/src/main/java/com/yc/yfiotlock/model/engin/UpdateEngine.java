package com.yc.yfiotlock.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.user.UpgradeInfo;

import rx.Observable;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public class UpdateEngine extends BaseEngin {

    public UpdateEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.UPDATE_URL;
    }

    public Observable<ResultInfo<UpgradeInfo>> getUpdateInfo() {
        return rxpost(new TypeReference<ResultInfo<UpgradeInfo>>() {
                }.getType(),
                null, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG, Config.RESQUEST_FLAG);
    }
}
