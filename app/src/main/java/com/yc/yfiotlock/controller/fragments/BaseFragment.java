package com.yc.yfiotlock.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.controller.activitys.base.ILoadData;
import com.yc.yfiotlock.model.bean.EventStub;
import com.yc.yfiotlock.utils.CommonUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment  implements ILoadData {


    protected View mRootView;

    public BaseFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.msg("onCreate");

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       if (mRootView == null) {
            mRootView = View.inflate(getActivity(), getLayoutId(), null);
            ButterKnife.bind(this, mRootView);


            initVars();
            initViews();
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }



        }
        return mRootView;
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected void initVars() {
    }

    // eventbus stub
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStub(EventStub stub) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRootView = null;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    protected boolean isLoadedCache;



    @Override
    public void startActivity(Intent intent) {
        Activity activity = getActivity();
        if (CommonUtils.isActivityDestory(activity)) {
            super.startActivity(intent);
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    public void success(Object data) {

    }

    @Override
    public void fail() {

    }

    @Override
    public void empty() {

    }
}
