package com.yc.yfiotlock.controller.dialogs.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateIconDialog extends BottomSheetDialog {
    @BindView(R.id.tv_camera)
    TextView mTvCamera;
    @BindView(R.id.tv_pics)
    TextView mTvPics;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.cl_dialog)
    ConstraintLayout mDialogCl;
    @BindView(R.id.cl_root)
    ConstraintLayout mRootCl;

    public UpdateIconDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initViews();
    }


    int getLayoutId() {
        return R.layout.dialog_update_icon;
    }


    @SuppressLint("ClickableViewAccessibility")
    void initViews() {
        getWindow().setWindowAnimations(R.style.dialogButtonAnim);
        mRootCl.setOnTouchListener((v, event) -> { //点击除底部区域时关闭dialog
            if (event.getY() < ScreenUtil.getHeight(getContext()) - mDialogCl.getMeasuredHeight()) {
                dismiss();
            }
            return false;
        });
    }

    public interface OnTvClickListener {
        void camera();

        void pics();
    }

    private OnTvClickListener mOnTvClickListener;

    public void setOnTvClickListener(OnTvClickListener onTvClickListener) {
        mOnTvClickListener = onTvClickListener;
    }

    @OnClick({R.id.tv_camera, R.id.tv_pics, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_camera:
                if (mOnTvClickListener != null)
                    mOnTvClickListener.camera();
                dismiss();
                break;
            case R.id.tv_pics:
                if (mOnTvClickListener != null)
                    mOnTvClickListener.pics();
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
