package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.eventbus.FamilyAddEvent;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.engin.HomeEngine;
import com.yc.yfiotlock.view.widgets.RightNextTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observer;

public class MyFamilyModifyActivity extends MyFamilyAddActivity {

    public static void start(Context context,@NotNull FamilyInfo familyInfo) {
        Intent intent = new Intent(context, MyFamilyModifyActivity.class);
        intent.putExtra("family_info", familyInfo);
        context.startActivity(intent);
    }

    @Override
    protected void initVars() {
        super.initVars();
        familyInfo = (FamilyInfo)getIntent().getSerializableExtra("family_info");
    }

    @Override
    protected void initViews() {
        super.initViews();
        tvName.setTvDes(familyInfo.getName(), Color.parseColor("#000000"));
        tvLocation.setTvDes(familyInfo.getAddress(), Color.parseColor("#000000"));
        tvAddress.setTvDes(familyInfo.getDetailAddress(), Color.parseColor("#000000"));
        backNavBar.setTitle(familyInfo.getName());
    }

    public void submit() {
        int id = familyInfo.getId();
        mLoadingDialog.show("修改中...");
        homeEngine.modifyFamily(id, familyInfo.getName(), familyInfo.getLongitude(),
                familyInfo.getLatitude(), familyInfo.getAddress(), familyInfo.getDetailAddress()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), e.getMessage());
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    EventBus.getDefault().post(familyInfo);
                    ToastUtil.toast2(MyFamilyModifyActivity.this, info.getMsg());
                    mLoadingDialog.dismiss();
                    finish();
                } else {
                    String msg = "更新出错";
                    msg = info != null && info.getMsg() != null ? info.getMsg() : msg;
                    ToastCompat.show(getContext(), msg);
                }
            }
        });
    }
}
