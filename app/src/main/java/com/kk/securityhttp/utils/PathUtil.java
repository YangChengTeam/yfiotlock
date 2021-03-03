package com.kk.securityhttp.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by zhangkai on 2017/2/16.
 */

public class PathUtil {
    public static String getConfigPath(Context context) {
        String baseDir = makeBaseDir(context);
        File dir = new File(baseDir + "/config");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }

    public static String createDir(Context context, String dirName) {
        String baseDir = makeBaseDir(context);
        File dir = new File(baseDir + dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }

    private static String makeBaseDir(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName());
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getAbsolutePath();
    }
}
