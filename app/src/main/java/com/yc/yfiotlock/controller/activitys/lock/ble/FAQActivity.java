package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.FAQInfo;
import com.yc.yfiotlock.model.engin.FAQEngine;
import com.yc.yfiotlock.view.adapters.FAQAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

public class FAQActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.tv_common_problem)
    TextView mTvCommonProblem;
    @BindView(R.id.rv_question)
    RecyclerView mRvQuestion;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;
    @BindView(R.id.view_line)
    View mViewLine;
    @BindView(R.id.stv_feed_back)
    SuperTextView mStvFeedBack;

    @Override
    protected int getLayoutId() {
        return R.layout.ble_lock_activity_f_a_q;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(this::loadData);
        setRvQuestion();
        loadData();
    }

    @Override
    protected void initVars() {
        mFAQEngine = new FAQEngine(this);
    }

    private FAQEngine mFAQEngine;

    private void loadData() {
        mFAQEngine.getList(0).subscribe(new Observer<ResultInfo<List<FAQInfo>>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onNext(ResultInfo<List<FAQInfo>> listResultInfo) {

            }
        });
    }

    private FAQAdapter mFAQAdapter;

    private void setRvQuestion() {
        mFAQAdapter = new FAQAdapter(null);
        mRvQuestion.setAdapter(mFAQAdapter);
        mRvQuestion.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @OnClick(R.id.stv_feed_back)
    public void onViewClicked() {
        startActivity(new Intent(getContext(), FeedBackActivity.class));
    }
}