package com.yc.yfiotlock.controller.dialogs;

import android.content.Context;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

public class SuccessDialog extends BaseDialog {

    @BindView(R.id.message)
    TextView mMessageTv;


    private boolean canCancel;

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        setCanceledOnTouchOutside(canCancel);
    }

    public SuccessDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_success;
    }

    public void show(String message) {
        mMessageTv.setText(message);
        if (!isShowing()) {
            show();
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
