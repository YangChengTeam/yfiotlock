package com.kk.securityhttp.net.utils;

import android.text.TextUtils;

import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.kk.securityhttp.net.entry.Response;
import com.kk.securityhttp.net.entry.UpFileInfo;
import com.kk.securityhttp.utils.EncryptUtil;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.security.Encrypt;
import com.kk.securityhttp.utils.security.Md5;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhangkai on 16/9/9.
 */
public final class OKHttpUtil {

    //< 获取OkHttpClient.Builder
    public static OkHttpClient.Builder getHttpClientBuilder() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(HttpConfig.TIMEOUT, TimeUnit.MILLISECONDS);
        return builder;
    }

    //< 设置响应返回信息
    public static Response setResponse(int code, String body) {
        Response response = new Response();
        response.code = code;
        response.body = body;
        return response;
    }


    //< 添加http头信息
    public static void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (null != next && !TextUtils.isEmpty(next.getValue())) {
                    builder.addHeader(next.getKey(), next.getValue());
                }
//                iterator.remove();
            }
        }
    }

    //< 根据Map构建url信息
    public static String buildUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params != null && params.size() > 0) {
            boolean hasParams = false;
            if (url.contains("?")) {
                hasParams = true;
            }
            setDefaultParams(params, false);
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (null != next && !TextUtils.isEmpty(next.getValue())) {
                    if (i == 0 && !hasParams) {
                        urlBuilder.append("?" + next.getKey() + "=" + next.getValue());
                    } else {
                        urlBuilder.append("&" + next.getKey() + "=" + next.getValue());
                    }
                }
                i++;
//                iterator.remove();
            }
        }

        LogUtil.msg("客户端请求url->" + urlBuilder.toString());
        return urlBuilder.toString();
    }

    //< 获取Request.Builder
    public static Request.Builder getRequestBuilder(String url) {
        LogUtil.msg("客户端请求url->" + url);
        Request.Builder builder = new Request.Builder()
                .tag(url)
                .url(url);
        return builder;
    }


    //< 设置请求参数FormBody.Builder
    public static FormBody.Builder setBuilder(Map<String, String> params, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        setDefaultParams(params, isEncryptResponse);
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (null != next && !TextUtils.isEmpty(next.getValue())) {
                    builder.add(next.getKey(), next.getValue());
                }
            }
        }

        JSONObject jsonObject = new JSONObject(params);
        String jsonStr = jsonObject.toString();
        LogUtil.msg("客户端请求数据->" + jsonStr);

        return builder;
    }

    //< 设置请求参数MultipartBody.Builder
    public static MultipartBody.Builder setBuilder(UpFileInfo upFileInfo, Map<String, String> params, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        setDefaultParams(params, isEncryptResponse);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (params != null) {
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (null != next && !TextUtils.isEmpty(next.getValue())) {
                    builder.addFormDataPart(next.getKey(), next.getValue());
                }
            }
        }
        if (upFileInfo.file != null) {
            builder.addFormDataPart(upFileInfo.name, upFileInfo.filename, RequestBody.create(MediaType.parse
                            ("multipart/form-data"),
                    upFileInfo.file));
        } else if (upFileInfo.buffer != null) {
            builder.addFormDataPart(upFileInfo.name, upFileInfo.filename, RequestBody.create(MediaType.parse
                            ("multipart/form-data"),
                    upFileInfo.buffer));
        }
        JSONObject jsonObject = new JSONObject(params);
        String jsonStr = jsonObject.toString();
        LogUtil.msg("客户端请求数据->" + jsonStr);
        return builder;
    }


    //< 获取RequestBody
    public static RequestBody getRequestBody(Map<String, String> params, boolean isrsa, boolean
            isEncryptResponse) {
        byte[] data = OKHttpUtil.encodeParams(params, isrsa, isEncryptResponse);
        RequestBody requestBody = RequestBody.create(HttpConfig.MEDIA_TYPE, data);
        return requestBody;
    }

    //< 获取Request 1
    public static Request getRequest(String url, Map<String, String> params, Map<String, String> headers, boolean isrsa,
                                     boolean iszip, boolean isEncryptResponse) {
        Request.Builder builder = OKHttpUtil.getRequestBuilder(url);
        OKHttpUtil.addHeaders(builder, headers);
        Request request;
        if (isrsa) {
            iszip = true;
        }
        if (iszip) {
            request = builder.post(OKHttpUtil.getRequestBody(params, isrsa, iszip)).build();
        } else {
            request = builder.post(OKHttpUtil.setBuilder(params, isEncryptResponse).build()).build();
        }
        return request;
    }

    //< 获取Request 2
    public static Request getRequest(String url, Map<String, String> params, Map<String, String> headers, UpFileInfo
            upFileInfo, boolean isEncryptResponse) {
        Request.Builder builder = OKHttpUtil.getRequestBuilder(url);
        OKHttpUtil.addHeaders(builder, headers);
        MultipartBody requestBody = OKHttpUtil.setBuilder(upFileInfo, params, isEncryptResponse).build();
        return builder.post(requestBody).build();
    }

    //< 获取Request 3
    public static Request getRequest(String url, Map<String, String> headers, MediaType type, String body) {
        RequestBody requestBody = RequestBody.create(type, body);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        OKHttpUtil.addHeaders(builder, headers);
        return builder.build();
    }

    //<  加密正文
    public static byte[] encodeParams(Map params, boolean isrsa, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        setDefaultParams(params, isEncryptResponse);
        JSONObject jsonObject = new JSONObject(params);
        String jsonStr = jsonObject.toString();
        LogUtil.msg("客户端请求数据->" + jsonStr);
        if (isrsa) {
            LogUtil.msg("当前公钥->" + GoagalInfo.get().getPublicKeyString());
            jsonStr = EncryptUtil.rsa(GoagalInfo.get().getPublicKeyString(), jsonStr);
        }
        return EncryptUtil.compress(jsonStr);
    }

    //< 不加密参数 正常请求正文
    public static Map<String, String> encodeParams(Map params, boolean isEncryptResponse) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        setDefaultParams(params, isEncryptResponse);
        JSONObject jsonObject = new JSONObject(params);
        String jsonStr = jsonObject.toString();
        LogUtil.msg("客户端请求数据->" + jsonStr);
        return params;
    }

    ///< 解密返回值
    public static String decodeBody(InputStream in) {
        return Encrypt.decode(EncryptUtil.unzip(in));
    }

    //设置默认参数
    private static void setDefaultParams(Map<String, String> params, boolean encryptResponse) {
        if (defaultParams != null) {
            params.putAll(defaultParams);
        }
    }

    public static void setDefaultParams(Map<String, String> params) {
        OKHttpUtil.defaultParams = params;
    }

    public static Map<String, String> getDefaultParams() {
        return OKHttpUtil.defaultParams;
    }

    private static Map<String, String> defaultParams;

    /**
     * 对params参数生成验签
     *
     * @param params 参数
     * @return 验签
     */
    public static String getApiSign(Map<String, String> params) {
        // 验签参数不要做encode处理
        String content = mapToUrlParams(params, true, true);
        com.kk.securityhttp.utils.security.Base64.encode(content.getBytes());
        return Md5.md5(com.kk.securityhttp.utils.security.Base64.encode(content.getBytes()));
    }

    /**
     * 将Map参数转换成URL，是否排序， 是否encode编码
     *
     * @param params Map参数
     * @param sort   是否排序
     * @param encode 是否encode编码
     * @return URL
     */
    public static String mapToUrlParams(Map<String, String> params, boolean sort, boolean encode) {
        if (params != null && !params.isEmpty()) {
            //移除可能为null的参数
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (null != next && TextUtils.isEmpty(next.getValue())) {
                    LogUtil.msg("OKHttpUtil-->mapToUrlParams-->移除为空的参数:" + next.getKey());
                    iterator.remove();
                }
            }
            try {
                ArrayList<String> keys = new ArrayList<>(params.keySet());
                if (sort) {
                    Collections.sort(keys);
                }
                StringBuilder buffer = new StringBuilder();
                if (encode) {
                    for (String key : keys) {
                        buffer.append("&");
                        buffer.append(URLEncoder.encode(key, "UTF-8"));
                        buffer.append("=");
                        buffer.append(URLEncoder.encode(
                                getStringDef(params.get(key)), "UTF-8"));
                    }
                } else {
                    for (String key : keys) {
                        buffer.append("&");
                        buffer.append(key);
                        buffer.append("=");
                        buffer.append(getStringDef(params.get(key)));
                    }
                }
                return buffer.substring(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static String getStringDef(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }
}
