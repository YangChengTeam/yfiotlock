package com.yc.yfiotlock.model.bean.lock.remote;

public class ItemInfo {
    private String name;
    private String des;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ItemInfo(String name, String des, int id) {
        this.name = name;
        this.des = des;
        this.id = id;
    }
}
