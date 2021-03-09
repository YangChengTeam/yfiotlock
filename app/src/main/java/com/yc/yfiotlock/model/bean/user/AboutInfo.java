package com.yc.yfiotlock.model.bean.user;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class AboutInfo {
    private String name;
    private String value;

    public AboutInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
