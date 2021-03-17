package com.yc.yfiotlock.model.bean.lock.remote;

import java.util.List;

public class LogListInfo {
    private int total;
    private List<LogInfo> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LogInfo> getItems() {
        return items;
    }

    public void setItems(List<LogInfo> items) {
        this.items = items;
    }
}
