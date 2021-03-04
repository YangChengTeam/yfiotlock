package com.yc.yfiotlock.model.bean;

public class PassWordInfo {
    private String name;
    private String validity;
    private String stateDes;
    private int state;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getStateDes() {
        return stateDes;
    }

    public void setStateDes(String stateDes) {
        this.stateDes = stateDes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public PassWordInfo(String name, String validity, int state, int id) {
        this.name = name;
        this.validity = validity;
        this.state = state;
        this.id = id;
    }
}
