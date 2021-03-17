package com.yc.yfiotlock.model.bean.lock.remote;

import com.alibaba.fastjson.annotation.JSONField;
import com.yc.yfiotlock.R;

public class LogInfo {
    private int id;
    @JSONField(name = "pwd_type")
    private int pwdType;
    @JSONField(name = "group_type")
    private int groupType;
    @JSONField(name = "is_local")
    private int isLocal;
    @JSONField(name = "is_succ")
    private int isSucc;
    @JSONField(name = "action_time")
    private long actionTime;
    private int icon;
    @JSONField(name = "action_name")
    private String actionName;
    @JSONField(name = "add_time")
    private long addTime;
    private String desp;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPwdType() {
        return pwdType;
    }

    public void setPwdType(int pwdType) {
        this.pwdType = pwdType;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public int getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(int isLocal) {
        this.isLocal = isLocal;
    }

    public int getIsSucc() {
        return isSucc;
    }

    public void setIsSucc(int isSucc) {
        this.isSucc = isSucc;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public int getIcon() {
        return R.mipmap.icon_log;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
