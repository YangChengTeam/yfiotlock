package com.yc.yfiotlock.controller.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.yc.yfiotlock.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class GeneralDialog extends BaseDialog {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_negative)
    TextView mTvNegative;
    @BindView(R.id.tv_positive)
    TextView mTvPositive;
    @BindView(R.id.ll_btn)
    LinearLayout mLlBtn;

    public GeneralDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_general;
    }

    @Override
    protected void initViews() {

    }

    private OnBtnClickListener mOnBtnClickListener;

    public GeneralDialog setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
        mOnBtnClickListener = onBtnClickListener;
        return this;
    }

    public GeneralDialog setTitle(String text) {
        mTvTitle.setText(text);
        return this;
    }

    public GeneralDialog setMsg(String text) {
        mTvContent.setText(text);
        return this;
    }

    public GeneralDialog setNegativeText(String text) {
        mTvNegative.setText(text);
        return this;
    }

    public GeneralDialog setNegativeTextColor(@ColorInt int color) {
        mTvNegative.setTextColor(color);
        return this;
    }

    public GeneralDialog setPositiveText(String text) {
        mTvPositive.setText(text);
        return this;
    }

    public GeneralDialog setPositiveTextColor(@ColorInt int color) {
        mTvPositive.setTextColor(color);
        return this;
    }

    /**
     * @param dismissed if false ,onBackPressed useless
     * @return this
     */
    public GeneralDialog setOnBackPressedDismissed(boolean dismissed) {
        this.dismissed = dismissed;
        return this;
    }

    private boolean dismissed = true;

    @Override
    public void onBackPressed() {
        if (dismissed) {
            super.onBackPressed();
        }
    }

    /**
     * setting btn click listener
     */
    public interface OnBtnClickListener {

        void onPositiveClick();

        void onNegativeClick();
    }

    @OnClick({R.id.tv_negative, R.id.tv_positive})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_negative:
                if (mOnBtnClickListener != null) {
                    mOnBtnClickListener.onNegativeClick();
                }
                dismiss();
                break;
            case R.id.tv_positive:
                if (mOnBtnClickListener != null) {
                    mOnBtnClickListener.onPositiveClick();
                }
                dismiss();
                break;
            default:
                break;
        }
    }
}
