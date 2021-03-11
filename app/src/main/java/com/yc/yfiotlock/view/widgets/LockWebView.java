package com.yc.yfiotlock.view.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/11
 **/
public class LockWebView extends WebView {
    private boolean needClear = true;

    private String TAG = "LockWebView";

    public void setNeedClear(boolean needClear) {
        this.needClear = needClear;
    }

    public LockWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setDefaultTextEncodingName("UTF-8");


        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                if (mWebListener != null) {
                    mWebListener.onPageFinished();

                }
                if (needClear) {
                    view.clearHistory();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mWebListener != null) {
                    mWebListener.onError();
                }
                Log.i(TAG, "onReceivedError: " + request +"error:"+ error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                if (mWebListener != null) {
                    mWebListener.onError();
                    Log.i(TAG, "onReceivedSslError: " + handler +"error:"+ error);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (mWebListener != null) {
                    mWebListener.onError();
                    Log.i(TAG, "onReceivedError: errorCode:"
                            + errorCode + "description:" + description + "failingUrl:" + failingUrl);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mWebListener != null) {
                    if (!mWebListener.onShouldOverrideUrlLoading(view, url)) {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });


        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mWebListener != null) {
                    mWebListener.onProgressChanged(newProgress);
                }
            }
        });

    }

    private WebViewListener mWebListener;

    public void setAdWebViewListener(WebViewListener adWebViewListener) {
        this.mWebListener = adWebViewListener;
    }

    public LockWebView(Context context) {
        super(getFixedContext(context));
    }

    public LockWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LockWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(getFixedContext(context), attrs, defStyleAttr, defStyleRes);
    }

    private static Context getFixedContext(Context context) {
        // Android Lollipop 5.0 & 5.1
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            return context.createConfigurationContext(new Configuration());
        }
        return context;
    }

    public interface WebViewListener {

        /**
         * when page load finish called
         */
        void onPageFinished();

        /**
         * when page has redirected to a new url called
         *
         * @param view this webView
         * @param url  Redirect url
         * @return whether Intercept this redirect url
         */
        boolean onShouldOverrideUrlLoading(WebView view, String url);

        /**
         * when loading page called
         *
         * @param newProgress loading progress
         */
        void onProgressChanged(int newProgress);

        /**
         * when load fail called
         */
        void onError();
    }
}
