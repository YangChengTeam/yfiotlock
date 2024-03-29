package com.yc.yfiotlock.model.bean.user;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.utils.CommonUtil;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private int id;
    private String name;
    @JSONField(name = "nickname")
    private String nickName;
    private String face = "";
    private String sign;
    private String scene;
    private String model;

    private String mobile;
    /**
     * 智能设备的数量
     */
    @JSONField(name = "locker_number")
    private int deviceNumber;

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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        if (TextUtils.isEmpty(nickName)) {
            return getMobile();
        }
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        mobile = CommonUtil.setPhoneSecret(mobile);
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


    public int getDeviceNumber() {
        if (deviceNumber < 0) {
            deviceNumber = 0;
        }
        return deviceNumber;
    }

    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
