package com.yc.yfiotlock.compat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;


import com.yc.yfiotlock.utils.CommonUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ToastCompat {
    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        if (Build.VERSION.SDK_INT <= 24) {
            try {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);
                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("aaaa", "static initializer: " + e);
            }
        }
    }

    private static void hook(Toast toast) {
        if (Build.VERSION.SDK_INT > 24) return;
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(Context context, CharSequence cs, int length) {
        if (CommonUtils.isActivityDestory(context)) return;

        CharSequence text = cs + "";
        Toast toast = Toast.makeText(context, text, length);

        hook(toast);
        toast.show();
    }

    public static Toast makeToast(Context context, CharSequence cs, int length) {
        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(context, cs, length);
        hook(toast);
        return toast;
    }

    public static Toast makeToast(Context context, CharSequence cs) {
        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(context, cs, Toast.LENGTH_LONG);
        hook(toast);
        return toast;
    }

    public static void show(Context context, CharSequence cs) {
        show(context, cs, Toast.LENGTH_LONG);
    }

    public static void showCenter(Context context, CharSequence cs) {
        if (CommonUtils.isActivityDestory(context)) return;
        Toast toast = Toast.makeText(context, cs, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        hook(toast);
        toast.show();
    }

    public static class SafelyHandlerWarpper extends Handler {
        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);
        }
    }
}

