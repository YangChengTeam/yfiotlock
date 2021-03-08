package com.kk.securityhttp.engin;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.listeners.Callback;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.kk.securityhttp.net.entry.Response;
import com.kk.securityhttp.net.entry.UpFileInfo;
import com.kk.securityhttp.net.impls.OKHttpRequest;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;


import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhangkai on 2017/4/28.
 */

public abstract class BaseEngin<T> {
    private String key = "";
    private WeakReference<Context> mContext;

    public BaseEngin(Context context) {
        key = new Random().nextInt(10000) + "";
        mContext = new WeakReference<>(context);
    }

    public Context getContext() {
        if(mContext != null){
            return mContext.get();
        }
        return null;
    }

    public void cancel() {
        OKHttpRequest.getImpl().cancel(getUrl() + "&key=" + key);
    }

    //< 同步请求get 1
    private T get(Type type, Map<String, String> params, Map<String, String> headers, boolean isEncryptResponse) {
        T resultInfo = null;
        try {
            Response response = OKHttpRequest.getImpl().get(getUrl() + "&key=" + key, params, headers, isEncryptResponse);
            resultInfo = getResultInfo(response.body, type);
        } catch (Exception e) {
            LogUtil.msg("异常->" + e, LogUtil.W);
        }
        return resultInfo;
    }

    //< 同步请求rxjava get 3
    public Observable<T> rxget(final Type type, final Map<String, String> params, final Map<String, String> headers, final boolean
            isEncryptResponse) {
        return Observable.just("").map(new Func1<Object, T>() {
            @Override
            public T call(Object o) {
                return get(type, params, headers, isEncryptResponse);
            }
        }).subscribeOn(Schedulers.newThread()).onErrorReturn(new Func1<Throwable, T>() {
            @Override
            public T call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    //< 同步请求rxjava get 2
    public Observable<T> rxget(final Type type, final Map<String, String> params, final boolean
            isEncryptResponse) {
        return rxget(type, params, null, isEncryptResponse);
    }

    //< 同步请求rxjava get 1
    public Observable<T> rxget(final Type type, final boolean isEncryptResponse) {
        return rxget(type, null, isEncryptResponse);
    }

    //< 同步请求post 1
    private T post(Type type, Map<String, String> params, Map<String, String> headers, boolean
            isrsa, boolean iszip, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<>();
        }
        T resultInfo = null;
        try {
            Response response = OKHttpRequest.getImpl().post(getUrl() + "&key=" + key, params, headers, isrsa, iszip, isEncryptResponse);
            resultInfo = getResultInfo(response.body, type);
        } catch (Exception e) {
            String body = "{\"code\":500, \"message\":\"" + e.getMessage().replaceAll("\"", "'") + "\"}";
            resultInfo = getResultInfo(body, type);
            Log.i("securityhttp", "post: resultInfo"+resultInfo.toString());
            LogUtil.msg("异常->" + e, LogUtil.W);
        }
        return resultInfo;
    }


    //< 同步请求rxjava post 2
    public Observable<T> rxpost(final Type type, final Map<String, String>
            params, final boolean isrsa, final boolean iszip, final boolean isEncryptResponse) {
        return rxpost(type, params, null, isrsa, iszip, isEncryptResponse);
    }


    //< 同步请求rxjava post 1
    public Observable<T> rxpost(final Type type, final Map<String, String>
            params, final Map<String, String>
                                        headers, final boolean isrsa, final boolean iszip, final boolean isEncryptResponse) {
        return Observable.just("").map(new Func1<Object, T>() {
            @Override
            public T call(Object o) {
                return post(type, params, headers, isrsa, iszip, isEncryptResponse);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, T>() {
            @Override
            public T call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    //< 同步请求rxjava post string
    public Observable<String> rxpost(final Map<String, String> header, final MediaType type, final String
            body) {
        return Observable.just("").map(new Func1<Object, String>() {
            @Override
            public String call(Object o) {
                String data = "";
                try {
                    Response response = OKHttpRequest.getImpl().post(getUrl() + "&key=" + key, header, type, body);
                    if (response != null) {
                        data = response.body;
                    }
                } catch (Exception e) {
                    LogUtil.msg("异常->" + e, LogUtil.W);
                }
                return data;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, String>() {
            @Override
            public String call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    //< 同步请求rxjava post string no header
    public Observable<String> rxpost(final MediaType type, final String
            body) {
        return rxpost(null, type, body);
    }

    //< 同步请求rxjava post json
    public Observable<String> rxpost(final Map<String, String> header, final String
            json) {
        return rxpost(header, MediaType.parse("application/json; charset=utf-8"), json);
    }

    //< 同步请求rxjava post json no header
    public Observable<String> rxpost(final String
                                             json) {
        return rxpost(null, MediaType.parse("application/json; charset=utf-8"), json);
    }


    //< 同步请求uploadFile 1
    public T uploadFile(Type type, UpFileInfo
            upFileInfo, Map<String, String>
                                params, Map<String, String> headers, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<>();
        }
        T resultInfo = null;
        try {
            Response response = OKHttpRequest.getImpl().uploadFile(getUrl() + "&key=" + key, upFileInfo, params,
                    headers, isEncryptResponse);
            resultInfo = JSON.parseObject(response.body, type);
        } catch (Exception e) {
            LogUtil.msg("异常->" + e, LogUtil.W);
        }
        return resultInfo;
    }

    //< 异步请求rxuploadFile 2
    public Observable<T> rxuploadFile(final Type type, final UpFileInfo upFileInfo, final Map<String, String>
            params, final boolean isEncryptResponse) {
        return rxuploadFile(type, upFileInfo, params, null, isEncryptResponse);
    }

    //< 异步请求rxuploadFile 1
    public Observable<T> rxuploadFile(final Type type, final UpFileInfo upFileInfo, final Map<String, String>
            params, final Map<String, String>
                                              headers, final boolean isEncryptResponse) {
        return Observable.just("").map(new Func1<String, T>() {
            @Override
            public T call(String s) {
                return uploadFile(type, upFileInfo, params, headers, isEncryptResponse);
            }
        }).subscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers
                .mainThread()).onErrorReturn(new Func1<Throwable, T>() {
            @Override
            public T call(Throwable throwable) {
                LogUtil.msg(throwable.getMessage());
                return null;
            }
        });
    }

    private void success(final Callback callback, final T lastResultInfo) {
        VUiKit.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(lastResultInfo);
            }
        });
    }

    private void failure(final Callback callback, final Response response) {
        VUiKit.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(response);
            }
        });
    }

    private void aerror(Exception e, Callback callback, Response response) {
        LogUtil.msg("异常->" + e, LogUtil.W);
        e.printStackTrace();
        if (response == null) {
            response = new Response();
            response.body = e.getMessage();
            response.code = HttpConfig.SERVICE_ERROR_CODE;
        }
        if (callback != null) {
            failure(callback, response);
        }
    }

    private void aerror(Exception e, Callback callback) {
        aerror(e, callback, null);
    }


    private T getResultInfo(String body, Type type) {
        T resultInfo;
        if (type != null) {
            resultInfo = JSON.parseObject(body, type);
        } else {
            resultInfo = JSON.parseObject(body, new TypeReference<T>() {
            }); //范型已被擦除 --！
        }
        return resultInfo;
    }

    public abstract String getUrl();

}
