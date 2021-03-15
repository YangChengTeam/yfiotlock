package com.yc.yfiotlock.model.bean;

import com.yc.yfiotlock.R;

public class WarnInfo {
    private int id;
    private String action_name;
    private long action_time;
    private long add_time;
    private String desp;
    private String msg_conent;
    private int icon;

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public long getAction_time() {
        return action_time;
    }

    public void setAction_time(long action_time) {
        this.action_time = action_time;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getMsg_conent() {
        return msg_conent;
    }

    public void setMsg_conent(String msg_conent) {
        this.msg_conent = msg_conent;
    }

    public int getIcon() {
        return R.mipmap.alarm;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
