package com.yc.yfiotlock.libs.fastble.callback;


import com.yc.yfiotlock.libs.fastble.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
