package com.yc.yfiotlock.model.bean.user;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

public class UserInfo {

    private String id;
    private String name;
    @JSONField(name = "nickname")
    private String nickName;
    private String face;
    private String sign;
    private String scene;
    private String model;

    private String mobile;
    private String deviceNumber;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return getMobile();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }



    public String getDeviceNumber() {
        return deviceNumber == null ? "1" : deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
