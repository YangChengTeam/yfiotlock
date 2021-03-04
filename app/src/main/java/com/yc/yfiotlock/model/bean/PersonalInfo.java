package com.yc.yfiotlock.model.bean;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class PersonalInfo {
    private String name;
    private int resId;

    public PersonalInfo(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
