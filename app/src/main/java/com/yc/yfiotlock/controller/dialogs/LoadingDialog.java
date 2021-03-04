package com.yc.yfiotlock.controller.dialogs;

import android.content.Context;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

/*
 * Created by　Dullyoung on 2021/3/4
 */
public class LoadingDialog extends BaseDialog {

    @BindView(R.id.message)
    TextView mMessageTv;



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
