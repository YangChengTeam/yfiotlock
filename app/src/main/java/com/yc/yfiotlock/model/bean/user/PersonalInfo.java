package com.yc.yfiotlock.model.bean.user;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class PersonalInfo {
    private String name;
    private String value;
    private String img;
    private int type;//0图片 1文字
    private boolean showArrow = true;
    private int resId;

    public PersonalInfo(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }

    public PersonalInfo(String name, String value, String img, int type) {
        this.name = name;
        this.value = value;
        this.img = img;
        this.type = type;
    }


    public boolean isShowArrow() {
        return showArrow;
    }

    public PersonalInfo setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
