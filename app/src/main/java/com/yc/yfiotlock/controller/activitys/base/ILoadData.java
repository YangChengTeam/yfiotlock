package com.yc.yfiotlock.controller.activitys.base;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public interface ILoadData {
    void success(Object data);

    void fail();

    void empty();
}
