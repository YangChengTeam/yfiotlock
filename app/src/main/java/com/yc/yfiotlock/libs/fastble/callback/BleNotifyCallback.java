package com.yc.yfiotlock.libs.fastble.callback;


import com.yc.yfiotlock.libs.fastble.exception.BleException;

public abstract class BleNotifyCallback extends BleBaseCallback {

    public abstract void onNotifySuccess();

    public abstract void onNotifyFailure(BleException exception);

    public abstract void onCharacteristicChanged(byte[] data);

}
