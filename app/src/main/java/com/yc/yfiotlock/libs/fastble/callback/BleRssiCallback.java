package com.yc.yfiotlock.libs.fastble.callback;


import com.yc.yfiotlock.libs.fastble.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}