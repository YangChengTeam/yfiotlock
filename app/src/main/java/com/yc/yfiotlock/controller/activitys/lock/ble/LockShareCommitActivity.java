package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.ShareDeviceEngine;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class LockShareCommitActivity extends BaseBackActivity {


    @BindView(R.id.iv_face)
    ImageView mIvFace;
    @BindView(R.id.tv_account)
    TextView mTvAccount;
    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.tv_sure)
    TextView mTvSure;

    public static void start(Context context, DeviceInfo deviceInfo, UserInfo userInfo) {
        Intent intent = new Intent(context, LockShareCommitActivity.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("DeviceInfo", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_lock_share_commit;
    }

    UserInfo mUserInfo;
    DeviceInfo lockInfo;

    @Override
    protected void initViews() {
        super.initViews();
        lockInfo = (DeviceInfo) getIntent().getSerializableExtra("DeviceInfo");
        String text = "<font color='#999999'>允许控制</font><font color='#222222'>【" + lockInfo.getName() + "】</font>";
        mTvResult.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        mUserInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if (mUserInfo == null) {
            finish();
            ToastCompat.show(getContext(), "用户信息有误");
            return;
        }
        backNavBar.setTitle(lockInfo.getName().concat("共享管理"));
        mTvAccount.setText(mUserInfo.getNickName());
        Glide.with(getContext())
                .load(mUserInfo.getFace())
                .error(R.mipmap.head_big)
                .placeholder(R.mipmap.head_big)
                .circleCrop()
                .into(mIvFace);
    }

    @Override
    protected void bindClick() {
        setClick(mTvSure, this::shareDevice);
    }

    @Override
    protected void initVars() {
        super.initVars();
        mEngine = new ShareDeviceEngine(getContext());
    }

    private ShareDeviceEngine mEngine;

    private void shareDevice() {
        mLoadingDialog.show("分享中...");
        String msg = "分享失败";
        mEngine.shareDevice(mUserInfo.getId(), lockInfo.getId() + "").subscribe(new Observer<ResultInfo<String>>() {
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
                ToastCompat.show(getContext(), info.getMsg());
                if (info.getCode() == 1) {
                    mLoadingDialog.dismiss();
                    EventBus.getDefault().post(ShareDeviceEngine.SHARE_DEVICE_SUCCESS);
                    finish();
                } else {
                    String tmsg = msg;
                    tmsg = info != null && info.getMsg() != null ? info.getMsg() : tmsg;
                    ToastCompat.show(getContext(), tmsg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.cancelAll();
        }
    }
}