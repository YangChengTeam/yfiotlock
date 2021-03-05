package com.yc.yfiotlock.model.bean;

import android.graphics.drawable.Drawable;

public class LogInfo {
    private String name;
    private String date;
    private String des;
    private int id;
    private int icon;


    public LogInfo(String name, String date, String des, int icon, int id) {
        this.name = name;
        this.date = date;
        this.des = des;
        this.icon = icon;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
