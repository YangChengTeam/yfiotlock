package com.yc.yfiotlock.controller.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class LoadingDialog extends BaseDialog {

    @BindView(R.id.message)
    TextView mMessageTv;
    @BindView(R.id.iv_icon_loading)
    ImageView ivIcon;
    @BindView(R.id.pd_loading)
    ProgressBar progressBar;


    private boolean canCancel;

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        setCanceledOnTouchOutside(canCancel);
    }

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    public void show(String message) {
        mMessageTv.setText(message);
        show();
    }

    @Override
    public void dismiss() {
        if (ivIcon != null && ivIcon.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
            ivIcon.setVisibility(View.GONE);
        }
        super.dismiss();
    }

    public void setIcon(int icon) {
        progressBar.setVisibility(View.GONE);
        ivIcon.setVisibility(View.VISIBLE);
        if (icon > 0) {
            ivIcon.setImageDrawable(getContext().getDrawable(icon));
        }
    }

    public void setMessageTv(String msg) {
        mMessageTv.setText(msg);
    }

    @Override
    public void onBackPressed() {
        if (canCancel) {
            super.onBackPressed();
        }
    }

    @Override
    protected void initViews() {
        canCancel = true;
    }
}
