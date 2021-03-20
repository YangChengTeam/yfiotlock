package com.yc.yfiotlock.controller.activitys.user;

import android.view.WindowManager;
import android.widget.EditText;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.user.UserInfo;
import com.yc.yfiotlock.model.engin.UserEngine;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.utils.UserInfoCache;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;

/**
 * @author Dullyoung
 */
public class EditNameActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;

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
            name = UserInfoCache.getUserInfo().getNickName();
        }
        CommonUtil.setEditTextLimit(mEtName, 20, true);
        mEtName.setText(name);
        mEtName.setSelection(name.length());
        mEtName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void initVars() {
        super.initVars();
        mUserEngine = new UserEngine(getContext());
    }

    UserEngine mUserEngine;

    @Override
    protected void bindClick() {
        setClick(R.id.stv_sure, this::commit);
    }

    public void commit() {
        if (mEtName.getText().toString().length() == 0) {
            ToastCompat.showCenter(getContext(), "用户名不能为空");
            return;
        }
        mLoadingDialog.show("提交中...");
        mUserEngine.changeNickName(mEtName.getText().toString()).subscribe(new Observer<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onNext(ResultInfo<String> info) {
                if (info.getCode() == 1 && App.isLogin()) {
                    mLoadingDialog.dismiss();
                    UserInfo userInfo = UserInfoCache.getUserInfo();
                    userInfo.setNickName(mEtName.getText().toString());
                    UserInfoCache.setUserInfo(userInfo);
                    EventBus.getDefault().post(userInfo);
                    ToastCompat.showCenter(getContext(), "修改成功");
                    finish();
                } else {
                    ToastCompat.showCenter(getContext(), info.getMsg());
                }
            }
        });
    }
}