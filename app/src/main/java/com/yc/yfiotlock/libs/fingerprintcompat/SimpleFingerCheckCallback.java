package com.yc.yfiotlock.libs.fingerprintcompat;

import android.util.Log;

/**
 * @author codersun
 * @time 2019/9/8 16:36
 */
public abstract class SimpleFingerCheckCallback implements IonFingerCallback {

    @Override
    public void onError(int code, String error) {
        Log.i("SimpleFingerCheckCallback", "onError: " + code + error);
    }

    @Override
    public void onHelp(String help) {

    }

    @Override
    public void onFailed() {

    }
}
