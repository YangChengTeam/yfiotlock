package com.yc.yfiotlock.model.bean;

import java.util.List;

public class WarnListInfo {
    private int total;
    private List<WarnInfo> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<WarnInfo> getItems() {
        return items;
    }

    public void setItems(List<WarnInfo> items) {
        this.items = items;
    }
}
