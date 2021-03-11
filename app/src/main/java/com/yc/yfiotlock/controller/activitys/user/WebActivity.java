package com.yc.yfiotlock.controller.activitys.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.LockWebView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebActivity extends BaseActivity implements LockWebView.WebViewListener {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.pb_process)
    ProgressBar mPbProcess;
    @BindView(R.id.lwv_page)
    LockWebView mLwvPage;
    @BindView(R.id.view_nowifi)
    NoWifiView mViewNowifi;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_web;
    }

    public static void start(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        String title = getIntent().getStringExtra("title");
        mBnbTitle.setTitle(title == null ? "" : title);
        mLwvPage.setAdWebViewListener(this);
        String url = getIntent().getStringExtra("url");
        if (url == null && title != null) {
            if (title.contains(getResources().getString(R.string.user_agreement))) {
                url = Config.USER_AGREEMENT;
            }
            if (title.contains(getResources().getString(R.string.privacy_policy))) {
                url = Config.PRIVACY_POLICY;
            }
        }
        if (url == null) {
            ToastCompat.showCenter(getContext(), "数据异常，请重试");
            finish();
            return;
        }
        mLwvPage.loadUrl(url);
        mViewNowifi.setMsg("加载出错了，请点击重试");
    }


    @OnClick(R.id.view_nowifi)
    public void onViewClicked() {
        mLwvPage.reload();
        isLoadSuccess=true;
    }

    @Override
    public void onPageFinished() {
        if (isLoadSuccess) {
            mViewNowifi.setVisibility(View.GONE);
            mPbProcess.setVisibility(View.GONE);
            mLwvPage.setVisibility(View.VISIBLE);
        } else {
            mLwvPage.setVisibility(View.GONE);
            mPbProcess.setVisibility(View.GONE);
            mViewNowifi.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onShouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onProgressChanged(int newProgress) {
        if (newProgress == 100) {
            mPbProcess.setVisibility(View.GONE);
            if (isLoadSuccess) {
                mViewNowifi.setVisibility(View.GONE);
                mPbProcess.setVisibility(View.GONE);
                mLwvPage.setVisibility(View.VISIBLE);
            } else {
                mLwvPage.setVisibility(View.GONE);
                mPbProcess.setVisibility(View.GONE);
                mViewNowifi.setVisibility(View.VISIBLE);
            }
        } else {
            mPbProcess.setVisibility(View.VISIBLE);
            mLwvPage.setVisibility(View.GONE);
            mViewNowifi.setVisibility(View.GONE);
        }
    }

    private boolean isLoadSuccess = true;

    @Override
    public void onError() {
        mPbProcess.setVisibility(View.GONE);
        mViewNowifi.setVisibility(View.VISIBLE);
        isLoadSuccess = false;
        mLwvPage.setVisibility(View.GONE);
    }
}