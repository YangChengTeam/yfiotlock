package com.yc.yfiotlock.model.bean.lock.remote;

import com.alibaba.fastjson.annotation.JSONField;
import com.yc.yfiotlock.R;

public class WarnInfo {
    private int id;
    @JSONField(name = "action_name")
    private String actionName;
    @JSONField(name = "action_time")
    private long actionTime;
    @JSONField(name = "add_time")
    private long addTime;
    private String desp;
    @JSONField(name = "msg_conent")
    private String msgConent;
    private int icon;

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getMsgConent() {
        return msgConent;
    }

    public void setMsgConent(String msgConent) {
        this.msgConent = msgConent;
    }

    public int getIcon() {
        return R.mipmap.alarm;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
