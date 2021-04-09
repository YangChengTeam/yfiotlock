package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.Toast;

import com.coorchice.library.SuperTextView;
import com.jakewharton.rxbinding4.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.ToastUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;
import com.yc.yfiotlock.model.engin.HomeEngine;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.RightNextTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observer;

public class MyFamilyAddActivity extends BaseActivity {

    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_family_add_name)
    RightNextTextView tvName;
    @BindView(R.id.tv_family_add_location)
    RightNextTextView tvLocation;
    @BindView(R.id.tv_family_add_address)
    RightNextTextView tvAddress;
    @BindView(R.id.stv_add)
    SuperTextView stvAdd;

    private FamilyInfo familyInfo;
    private HomeEngine homeEngine;

    public static void start(Context context, FamilyInfo familyInfo) {
        Intent intent = new Intent(context, MyFamilyAddActivity.class);
        if (familyInfo != null) {
            intent.putExtra("family_info", familyInfo);
        }
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_my_family_add;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> onBackPressed());

        initData();

        RxView.clicks(tvName).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            MyFamilyNameActivity.start(MyFamilyAddActivity.this, familyInfo);
        });
        RxView.clicks(tvLocation).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
            location();
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

    private void submit() {
        int id = familyInfo.getId();
        mLoadingDialog.show("提交中");
        if (id <= 0) {
            homeEngine.addFamily(familyInfo.getName(), familyInfo.getLongitude(),
                    familyInfo.getLatitude(), familyInfo.getAddress(), familyInfo.getDetailAddress()).subscribe(new Observer<ResultInfo<String>>() {
                @Override
                public void onCompleted() {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onNext(ResultInfo<String> stringResultInfo) {
                    ToastUtil.toast2(MyFamilyAddActivity.this, stringResultInfo.getMsg());

                    String data = stringResultInfo.getData();

                    if (!TextUtils.isEmpty(data)) {
                        familyInfo.setId(Integer.parseInt(data));
                    }

                    familyInfo.setUpdateList(true);
                    EventBus.getDefault().post(familyInfo);
                    finish();
                }
            });
        } else {
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
                        familyInfo.setUpdateList(true);
                        EventBus.getDefault().post(familyInfo);
                        ToastUtil.toast2(MyFamilyAddActivity.this, info.getMsg());
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

    private void location() {
        permissionHelper = new PermissionHelper();
        permissionHelper.checkAndRequestPermission(MyFamilyAddActivity.this, new PermissionHelper.OnRequestPermissionsCallback() {
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

    PermissionHelper permissionHelper;

    private void initData() {
        Serializable serializable = getIntent().getSerializableExtra("family_info");
        if (serializable instanceof FamilyInfo) {
            FamilyInfo familyInfo = (FamilyInfo) serializable;
            this.familyInfo = familyInfo;
            tvName.setTvDes(familyInfo.getName(), Color.parseColor("#000000"));
            tvLocation.setTvDes(familyInfo.getAddress(), Color.parseColor("#000000"));
            tvAddress.setTvDes(familyInfo.getDetailAddress(), Color.parseColor("#000000"));

            mBnbTitle.setTitle(familyInfo.getName());
        }
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
}
