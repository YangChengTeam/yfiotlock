package com.yc.yfiotlock.model.bean.lock.remote;

import com.yc.yfiotlock.R;

public class LogInfo {
    private int id;
    private int pwd_type;
    private int group_type;
    private int is_local;
    private int is_succ;
    private long action_time;
    private int icon;
    private String action_name;
    private long add_time;
    private String desp;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
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

    public int getPwd_type() {
        return pwd_type;
    }

    public void setPwd_type(int pwd_type) {
        this.pwd_type = pwd_type;
    }

    public int getGroup_type() {
        return group_type;
    }

    public void setGroup_type(int group_type) {
        this.group_type = group_type;
    }

    public int getIs_local() {
        return is_local;
    }

    public void setIs_local(int is_local) {
        this.is_local = is_local;
    }

    public int getIs_succ() {
        return is_succ;
    }

    public void setIs_succ(int is_succ) {
        this.is_succ = is_succ;
    }

    public long getAction_time() {
        return action_time;
    }

    public void setAction_time(long action_time) {
        this.action_time = action_time;
    }

    public int getIcon() {
        return R.mipmap.icon_log;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
