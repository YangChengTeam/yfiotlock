package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public class NoDeviceView extends BaseView {
    @BindView(R.id.tv_message)
    TextView mTvMessage;

    public NoDeviceView(Context context) {
        super(context);
    }

    private String msg;

    public NoDeviceView(Context context, String msg) {
        super(context);
        this.msg = msg;
    }


    public NoDeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_nodevice;
    }

    public void setMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            mTvMessage.setText(msg);
        }
    }

    @Override
    protected void initViews(Context context) {
        super.initViews(context);
        if (!TextUtils.isEmpty(msg)) {
            mTvMessage.setText(msg);
        }
    }
}
