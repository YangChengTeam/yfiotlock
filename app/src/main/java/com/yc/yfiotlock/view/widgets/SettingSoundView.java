package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.tencent.mmkv.MMKV;
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

    private int volume = 3;
    private String deviceMac = "";

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
        volume = MMKV.defaultMMKV().getInt(deviceMac + "volume", 3);
        onSelect(volume);
    }

    public void setVolume(int volume) {
        MMKV.defaultMMKV().putInt(deviceMac + "volume", volume);
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public void resetVolume() {
        onSelect(volume);
    }

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
    }

    @OnClick({R.id.ll_low, R.id.ll_mid, R.id.ll_high})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_low:
                onSelect(1);
                break;
            case R.id.ll_mid:
                onSelect(2);
                break;
            case R.id.ll_high:
                onSelect(3);
                break;
            default:
                break;
        }
    }

    public void onSelect(int index) {
        reset();
        switch (index) {
            case 1:
                mCvLow.setVisibility(VISIBLE);
                mTvLow.setTextColor(0xff3091F8);
                break;
            case 2:
                mCvMid.setVisibility(VISIBLE);
                mTvMid.setTextColor(0xff3091F8);
                break;
            case 3:
                mCvHigh.setVisibility(VISIBLE);
                mTvHigh.setTextColor(0xff3091F8);
                break;
            default:
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
