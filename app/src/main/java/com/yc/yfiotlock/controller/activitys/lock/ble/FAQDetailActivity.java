package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.FAQInfo;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FAQDetailActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_question)
    TextView mTvQuestion;
    @BindView(R.id.view_line)
    View mViewLine;
    @BindView(R.id.tv_answer)
    TextView mTvAnswer;
    @BindView(R.id.stv_feed_back)
    SuperTextView mStvFeedBack;

    public static void start(Context context, FAQInfo FAQInfo) {
        Intent intent = new Intent(context, FAQDetailActivity.class);
        intent.putExtra("info", FAQInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_f_a_q_detail;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        FAQInfo faqInfo = (FAQInfo) getIntent().getSerializableExtra("info");
        if (faqInfo == null) {
            ToastCompat.showCenter(getContext(), "数据异常，请刷新重试");
            finish();
            return;
        }
        mTvQuestion.setText(faqInfo.getQuestion());
        mTvAnswer.setText(faqInfo.getAnswer());
    }


    @OnClick(R.id.stv_feed_back)
    public void onViewClicked() {
        startActivity(new Intent(getContext(), FeedBackActivity.class));
    }
}