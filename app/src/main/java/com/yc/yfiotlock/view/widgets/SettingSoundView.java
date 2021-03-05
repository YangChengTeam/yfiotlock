package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.yc.yfiotlock.R;

import java.net.BindException;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class SettingSoundView extends BaseView {
    @BindView(R.id.tv_low)
    TextView mTvLow;
    @BindView(R.id.tv_mid)
    TextView mTvMid;
    @BindView(R.id.tv_high)
    TextView mTvHigh;
    @BindView(R.id.cv_low)
    CardView mCvLow;
    @BindView(R.id.cv_mid)
    CardView mCvMid;
    @BindView(R.id.cv_high)
    CardView mCvHigh;

    public SettingSoundView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_setting_sound;
    }

    @Override
    protected void initViews(Context context) {
        super.initViews(context);
        onSelect(0);
    }

    @OnClick({R.id.tv_low, R.id.tv_mid, R.id.tv_high})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_low:
                onSelect(0);
                break;
            case R.id.tv_mid:
                onSelect(1);
                break;
            case R.id.tv_high:
                onSelect(2);
                break;
        }
    }

    public void onSelect(int index) {
        reset();
        switch (index) {
            case 0:
                mCvLow.setVisibility(VISIBLE);
                mTvLow.setTextColor(0xff3091F8);
                break;
            case 1:
                mCvMid.setVisibility(VISIBLE);
                mTvMid.setTextColor(0xff3091F8);
                break;
            case 2:
                mCvHigh.setVisibility(VISIBLE);
                mTvHigh.setTextColor(0xff3091F8);
                break;
        }
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.onChange(index);
        }
    }

    OnSelectChangeListener mOnSelectChangeListener;

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        mOnSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListener {
        void onChange(int index);
    }

    private void reset() {
        mCvLow.setVisibility(GONE);
        mCvMid.setVisibility(GONE);
        mCvHigh.setVisibility(GONE);
        mTvLow.setTextColor(0xff222222);
        mTvMid.setTextColor(0xff222222);
        mTvHigh.setTextColor(0xff222222);
    }
}
