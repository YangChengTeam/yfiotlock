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

public class MyFamilyAddActivity extends BaseBackActivity {
    @BindView(R.id.tv_family_add_name)
    RightNextTextView tvName;
    @BindView(R.id.tv_family_add_location)
    RightNextTextView tvLocation;
    @BindView(R.id.tv_family_add_address)
    RightNextTextView tvAddress;
    @BindView(R.id.stv_add)
    SuperTextView stvAdd;

    protected FamilyInfo familyInfo;
    protected HomeEngine homeEngine;

    public static void start(Context context) {
        Intent intent = new Intent(context, MyFamilyAddActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_my_family_add;
    }

    @Override
    protected void initViews() {
        super.initViews();

        RxView.clicks(tvName).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyNameActivity.start(MyFamilyAddActivity.this, familyInfo);
        });

        RxView.clicks(tvLocation).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            nav2Location();
        });

        RxView.clicks(tvAddress).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyAddressActivity.start(MyFamilyAddActivity.this, familyInfo);
        });

        RxView.clicks(stvAdd).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            if (familyInfo == null || TextUtils.isEmpty(familyInfo.getName())) {
                ToastUtil.toast2(this, "家庭名称不能为空");
                return;
            }
            if (TextUtils.isEmpty(familyInfo.getAddress())) {
                ToastUtil.toast2(this, "家庭位置不能为空");
                return;
            }
            submit();
        });

        homeEngine = new HomeEngine(this);
    }

    public void submit() {
        mLoadingDialog.show("添加中...");
        homeEngine.addFamily(familyInfo.getName(), familyInfo.getLongitude(),
                familyInfo.getLatitude(), familyInfo.getAddress(), familyInfo.getDetailAddress()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
                finish();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), e.getMessage());
                finish();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info != null && info.getCode() == 1) {
                    String id = info.getData();
                    if (!TextUtils.isEmpty(id)) {
                        familyInfo.setId(Integer.parseInt(id));
                    }
                    EventBus.getDefault().post(new FamilyAddEvent(familyInfo));
                } else {
                    String msg = "添加失败";
                    msg = info != null && info.getMsg() != null ? info.getMsg() : msg;
                    ToastCompat.show(getContext(), msg);
                }

            }
        });
    }

    private void nav2Location() {
        mPermissionHelper.checkAndRequestPermission(MyFamilyAddActivity.this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                MyFamilyLocationActivity.start(MyFamilyAddActivity.this, familyInfo);
            }

            @Override
            public void onRequestPermissionError() {
                Toast.makeText(MyFamilyAddActivity.this, "授权失败, 无法获取到当前位置", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFamilyInfo(FamilyInfo familyInfo) {
        this.familyInfo = familyInfo;
        tvName.setTvDes(familyInfo.getName(), Color.parseColor("#000000"));
        tvLocation.setTvDes(familyInfo.getAddress(), Color.parseColor("#000000"));
        if (TextUtils.isEmpty(familyInfo.getDetailAddress())) {
            tvAddress.setTvDes("请输入详细地址（非必填）", Color.parseColor("#ff999999"));
        } else {
            tvAddress.setTvDes(familyInfo.getDetailAddress(), Color.parseColor("#000000"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onRequestPermissionsResult(this, resultCode);
    }
}
