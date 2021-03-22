package com.yc.yfiotlock.model.bean.lock.remote;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class PassWordInfo implements Serializable {
    private int id;
    private String name;
    @JSONField(name = "temporary_pwd_status")
    private int temporaryPwdStatus;
    private String pwd;
    private String model;
    private String stateDes;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemporaryPwdStatus() {
        return temporaryPwdStatus;
    }

    public void setTemporaryPwdStatus(int temporaryPwdStatus) {
        this.temporaryPwdStatus = temporaryPwdStatus;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStateDes() {
        return stateDes;
    }

    public void setStateDes(String stateDes) {
        this.stateDes = stateDes;
    }
}
