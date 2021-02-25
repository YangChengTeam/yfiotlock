package com.yc.yfiotlock;

import android.app.Application;
import android.util.Log;

import java.util.Hashtable;
import java.util.logging.Logger;


public class App extends Application {
    private static App app;
    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

}
