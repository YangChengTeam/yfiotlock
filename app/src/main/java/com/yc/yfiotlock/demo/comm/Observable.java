package com.yc.yfiotlock.demo.comm;


import com.yc.yfiotlock.libs.fastble.data.BleDevice;

public interface Observable {

    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(BleDevice bleDevice);
}
