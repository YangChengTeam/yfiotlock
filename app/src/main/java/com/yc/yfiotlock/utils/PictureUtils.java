package com.yc.yfiotlock.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.entry.Response;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class PictureUtils {
    public static void zipPic(Context context, List<Uri> uris, Callback<List<File>> callback) {
        if (uris.size() <= 0) {
            callback.onFailure(new Response());
            return;
        }
        String path1 = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        if (!new File(path1).exists()) {
            new File(path1).mkdir();
        }

        List<File> outputFiles = new ArrayList<>();
        List<File> files = new ArrayList<>();
        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            String fileName = path1 + "/" + "pic_" + i + ".png";
            File file = PathUtil.copyFileToPath(context, uri, fileName);
            files.add(file);
        }

        Luban.with(context)
                .ignoreBy(100)
                .setTargetDir(path1)
                .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                .load(files)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        outputFiles.add(file);
                        if (outputFiles.size() == files.size()) {
                            callback.onSuccess(outputFiles);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(new Response("" + e));
                    }
                }).launch();
    }

    public static void zipPic(Context context, File file, Callback<File> callback) {

        if (file.length() <= 0) {
            callback.onFailure(new Response("文件格式错误 length<=0"));
            return;
        }
        String path1 = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        if (!new File(path1).exists()) {
            new File(path1).mkdir();
        }

        Luban.with(context)
                .ignoreBy(100)
                .setTargetDir(path1)
                .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                .load(file)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        callback.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(new Response(e.getMessage()));
                    }
                }).launch();
    }
}
