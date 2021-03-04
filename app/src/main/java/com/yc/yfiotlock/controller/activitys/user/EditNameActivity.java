package com.yc.yfiotlock.controller.activitys.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.UserInfo;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.stv_sure)
    SuperTextView mSTvSure;
    @BindView(R.id.et_name)
    EditText mEtName;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_edit_name;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        String name = "";
        if (UserInfoCache.getUserInfo() != null) {
            name = UserInfoCache.getUserInfo().getName();
        }
        mEtName.setText(name);
        mEtName.setSelection(name.length());
    }


    @OnClick(R.id.stv_sure)
    public void onViewClicked() {
        UserInfo userInfo = UserInfoCache.getUserInfo();
        userInfo.setName(mEtName.getText().toString());
        UserInfoCache.setUserInfo(userInfo);
        EventBus.getDefault().post(userInfo);
        finish();
    }
}