package com.kk.securityhttp.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * 日志输出类
 *
 * @author zhangkai
 */
public class LogUtil {
    private static boolean DEBUG = true;

    public static void setDEBUG(boolean DEBUG) {
        LogUtil.DEBUG = DEBUG;
    }

    public static final int LEVEL = 2;// 日志输出级别
    public static final int V = 0;
    public static final int D = 1;
    public static final int I = 2;
    public static final int W = 3;
    public static final int E = 4;

    private static final String TAG = "YFIOTLOCK";

    public static void msg(String msg, int level) {
        if (!DEBUG) return;
        Logger.init(TAG);
        switch (level) {
            case V:
                Logger.v(msg);
                break;
            case D:
                Logger.d(msg);
                break;
            case I:
                Logger.i(msg);
                break;
            case W:
                Logger.w(msg);
                break;
            case E:
                Logger.e(msg);
                break;
            default:
                break;
        }
    }

    public static void msg(String msg) {
        if (!DEBUG) return;
        Logger.init(TAG);
        switch (LEVEL) {
            case V:
                Log.v(TAG, msg);
                break;
            case D:
                Log.d(TAG, msg);
                break;
            case I:
                Log.i(TAG, msg);
                break;
            case W:
                Log.w(TAG, msg);
                break;
            case E:
                Log.e(TAG, msg);
                break;
            default:
                break;
        }
    }
}
