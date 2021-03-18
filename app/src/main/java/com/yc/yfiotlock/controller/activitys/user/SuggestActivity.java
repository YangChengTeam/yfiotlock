package com.yc.yfiotlock.controller.activitys.user;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.engin.FeedBackEngine;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

public class SuggestActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.et_suggest)
    EditText mEtSuggest;
    @BindView(R.id.ll_et)
    LinearLayout mLlEt;
    @BindView(R.id.et_contact)
    EditText mEtContact;
    @BindView(R.id.stv_commit)
    SuperTextView mStvCommit;

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_suggest;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
    }

    private FeedBackEngine mFeedBackEngine;

    @Override
    protected void initVars() {
        super.initVars();
        mFeedBackEngine = new FeedBackEngine(getContext());
        setClick(R.id.stv_commit, () -> {
            if (mEtSuggest.getText().toString().length() < 10) {
                ToastCompat.showCenter(getContext(), "请输入您的建议，不少于十个字");
                return;
            }
            commit();
        });

        mEtSuggest.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    private void commit() {
        mLoadingDialog.show("提交中...");
        mFeedBackEngine.addInfo(mEtContact.getText().toString(),
                mEtSuggest.getText().toString(),
                "",
                "").subscribe(new Observer<ResultInfo<String>>() {
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
                if (info != null && info.getCode() == 1) {
                    ToastCompat.show(getContext(), "提交成功");
                    finish();
                } else {
                    ToastCompat.show(getContext(), info == null ? "提交失败" : info.getMsg());
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFeedBackEngine != null) {
            mFeedBackEngine.cancel();
        }
    }
}