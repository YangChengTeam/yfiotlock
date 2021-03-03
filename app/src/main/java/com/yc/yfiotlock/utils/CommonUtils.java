package com.yc.yfiotlock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.NonNull;

/*
 * Created by　Dullyoung on 2021/3/3
 */
public class CommonUtils {

    public static boolean isActivityDestory(Context context) {
        Activity activity = findActivity(context);
        return activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
    }

    public static Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

}
