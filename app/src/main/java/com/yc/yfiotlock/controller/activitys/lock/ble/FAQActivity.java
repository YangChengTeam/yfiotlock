package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;
import android.graphics.ImageFormat;
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
import com.yc.yfiotlock.utils.CommonUtils;
import com.yc.yfiotlock.view.adapters.FAQAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.NoDataView;
import com.yc.yfiotlock.view.widgets.NoWifiView;

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
        mSrlRefresh.setOnRefreshListener(() -> {
            p = 1;
            loadData();
        });
        setRvQuestion();
        loadData();
    }

    private int p = 1;

    @Override
    protected void initVars() {
        mFAQEngine = new FAQEngine(this);
    }

    private FAQEngine mFAQEngine;

    private void loadData() {
        mFAQEngine.getList(p).subscribe(new Observer<ResultInfo<List<FAQInfo>>>() {
            @Override
            public void onCompleted() {
                mSrlRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSrlRefresh.setRefreshing(false);
                fail();
            }

            @Override
            public void onNext(ResultInfo<List<FAQInfo>> info) {
                if (info != null && info.getCode() == 1) {
                    if (info.getData() == null || info.getData().size() == 0) {
                        empty();
                    } else {
                        success(info);
                    }
                } else {
                    fail();
                }
            }
        });
    }

    private FAQAdapter mFAQAdapter;

    private void setRvQuestion() {
        mFAQAdapter = new FAQAdapter(null);
        mRvQuestion.setAdapter(mFAQAdapter);
        mRvQuestion.setLayoutManager(new LinearLayoutManager(getContext()));
        CommonUtils.setItemDivider(getContext(), mRvQuestion);
        mFAQAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            p++;
            loadData();
        });
        mFAQAdapter.setOnItemClickListener((adapter, view, position) -> {
            FAQDetailActivity.start(getContext(), mFAQAdapter.getData().get(position));
        });
    }


    @OnClick(R.id.stv_feed_back)
    public void onViewClicked() {
        startActivity(new Intent(getContext(), FeedBackActivity.class));
    }

    @Override
    public void success(Object data) {
        List<FAQInfo> infos = ((ResultInfo<List<FAQInfo>>) data).getData();
        if (p == 1) {
            mFAQAdapter.setNewInstance(infos);
        } else {
            mFAQAdapter.addData(infos);
        }
        if (infos.size() < 20) {
            mFAQAdapter.getLoadMoreModule().loadMoreEnd();
        } else {
            mFAQAdapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    @Override
    public void fail() {
        if (mFAQAdapter.getData().size() == 0) {
            mFAQAdapter.setEmptyView(new NoWifiView(getContext()));
        } else {
            mFAQAdapter.getLoadMoreModule().loadMoreFail();
        }
    }

    @Override
    public void empty() {
        if (mFAQAdapter.getData().size() == 0) {
            mFAQAdapter.setEmptyView(new NoDataView(getContext()));
        } else {
            mFAQAdapter.getLoadMoreModule().loadMoreEnd();
        }
    }
}