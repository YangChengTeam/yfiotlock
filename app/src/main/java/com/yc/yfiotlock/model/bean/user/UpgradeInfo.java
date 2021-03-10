package com.yc.yfiotlock.model.bean.user;

import com.alibaba.fastjson.annotation.JSONField;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public class UpgradeInfo {
    private UpdateInfo upgrade;
    @JSONField(name = "kf_qq_qun")
    private String kfQqQun;
    @JSONField(name = "kf_email")
    private String kfEmail;
    @JSONField(name = "official_web")
    private String officialWeb;

    public String getKfQqQun() {
        return kfQqQun;
    }

    public void setKfQqQun(String kfQqQun) {
        this.kfQqQun = kfQqQun;
    }

    public String getKfEmail() {
        return kfEmail;
    }

    public void setKfEmail(String kfEmail) {
        this.kfEmail = kfEmail;
    }

    public String getOfficialWeb() {
        return officialWeb;
    }

    public void setOfficialWeb(String officialWeb) {
        this.officialWeb = officialWeb;
    }

    public UpdateInfo getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpdateInfo upgrade) {
        this.upgrade = upgrade;
    }
}
