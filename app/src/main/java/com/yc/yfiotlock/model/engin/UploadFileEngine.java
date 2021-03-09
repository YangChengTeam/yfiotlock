package com.yc.yfiotlock.model.engin;

import android.os.Build;
import android.util.Log;

import com.kk.securityhttp.domain.GoagalInfo;

import com.yc.yfiotlock.App;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class UploadFileEngine {

    private RequestCall mRequestCall;

    public void post(String url, Map<String, String> params, Callback callback) {
        mRequestCall = OkHttpUtils.post().params(params).url(url).build();
        mRequestCall.execute(callback);
    }

    public void uploadWithFile(String url, Map<String, String> params, String name, File file,
                               com.kk.securityhttp.listeners.Callback<String> callback) {
        String fileName = "pic" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
        if (name.equals("face")) {
            fileName = fileName.concat(".jpg");
        } else if (name.equals("game")) {
            fileName = fileName.concat(".apk");
        } else {
            fileName = fileName.concat(".png");
        }
        //设置http默认参数
        String agent_id = "1";
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
            agent_id = GoagalInfo.get().channelInfo.agent_id;
        }
        if (App.isLogin()) {
            params.put("sign", UserInfoCache.getUserInfo().getSign());
        }
        params.put("agent_id", agent_id);
        params.put("ts", System.currentTimeMillis() + "");
        params.put("device_type", "2");
        params.put("imeil", GoagalInfo.get().uuid);
        String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
        params.put("sv", sv);

        if (GoagalInfo.get().getPackageInfo() != null) {
            params.put("app_version", GoagalInfo.get().getPackageInfo().versionCode + "");
        }
        StringBuilder paramString = new StringBuilder("{");
        for (String s : params.keySet()) {
            paramString.append("\"").append(s).append("\":").append("\"").append(params.get(s)).append("\",");
        }
        paramString.append("}");
        Log.i("securityhttp", "UploadWithFile:请求地址 ------>" + url);
        Log.i("securityhttp", "UploadWithFile:请求参数 ------>" + paramString.toString() +
                "\n本地绝对路径:" + file.getAbsolutePath() + "\n大小:" + file.length() / 1024 + "kb");
        mRequestCall = OkHttpUtils.post().params(params).addFile(name, fileName, file).url(url).build();
        mRequestCall.execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {
                String s = response.body().string();
                Log.i("securityhttp", "UploadWithFile:返回数据------> " + s);
                return s;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (callback != null) {
                    callback.onFailure(new com.kk.securityhttp.net.entry.Response(e + ""));
                }
            }

            @Override
            public void onResponse(Object response, int id) {
                if (callback != null) {
                    callback.onSuccess(response.toString());
                }
                file.delete();
            }
        });
    }

    public void uploadBg(String url, Map<String, String> params, String name, File file, Callback callback) {
        String fileName = file.getName();

        //设置http默认参数
        String agent_id = "1";
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
            agent_id = GoagalInfo.get().channelInfo.agent_id;
        }
        params.put("agent_id", agent_id);
        params.put("ts", System.currentTimeMillis() + "");
        params.put("device_type", "2");
        params.put("imeil", GoagalInfo.get().uuid);
        String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
        params.put("sv", sv);

        if (GoagalInfo.get().getPackageInfo() != null) {
            params.put("app_version", GoagalInfo.get().getPackageInfo().versionCode + "");
        }
        StringBuilder paramString = new StringBuilder("{");
        for (String s : params.keySet()) {
            paramString.append("\"").append(s).append("\":").append("\"").append(params.get(s)).append("\",");
        }
        paramString.append("}");
        Log.i("securityhttp", "UploadWithFile:请求地址 :" + url);
        Log.i("securityhttp", "UploadWithFile:请求参数 :" + paramString + "\nfilePath:" + file.getAbsolutePath() + "\nSize:" + file.length());
        mRequestCall = OkHttpUtils.post().params(params).addFile(name, fileName, file).url(url).build();
        mRequestCall.execute(callback);
    }

    public void cancel() {
        if (mRequestCall != null) {
            mRequestCall.cancel();
        }
    }


    public void uploadFiles(String url, Map<String, String> params, String name, List<File> files, Callback callback) {

        //设置http默认参数
        String agent_id = "1";
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
            agent_id = GoagalInfo.get().channelInfo.agent_id;
        }
        params.put("agent_id", agent_id);
        params.put("ts", System.currentTimeMillis() + "");
        params.put("device_type", "2");
        params.put("imeil", GoagalInfo.get().uuid);
        String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
        params.put("sv", sv);

        if (GoagalInfo.get().getPackageInfo() != null) {
            params.put("av", GoagalInfo.get().getPackageInfo().versionCode + "");
        }
        String paramString = "{";
        for (String s : params.keySet()) {
            paramString += "\"" + s + "\":" + "\"" + params.get(s) + "\",";
        }

        PostFormBuilder formBuilder = OkHttpUtils.post().params(params);
        for (int i = 0; i < files.size(); i++) {
            formBuilder.addFile("img_" + i + 1, files.get(i).getName(), files.get(i));
            paramString += "\"img_" + (i + 1) + "\":" + "\"" + files.get(i).getAbsolutePath() + "\",";
        }

        paramString += "}";
        Log.i("securityhttp", "UploadWithFile:请求地址 :" + url);
        Log.i("securityhttp", "UploadWithFile:请求参数 :" + paramString);
        mRequestCall = formBuilder.url(url).build();
        mRequestCall.execute(callback);
    }


}


