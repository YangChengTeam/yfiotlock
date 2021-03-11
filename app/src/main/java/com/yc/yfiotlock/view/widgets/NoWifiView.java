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
public class NoWifiView extends BaseView {
    @BindView(R.id.tv_message)
    TextView mTvMessage;

    public NoWifiView(Context context) {
        super(context);
    }

    public NoWifiView(Context context, String msg) {
        super(context);
        this.msg = msg;
    }

    public NoWifiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private String msg;

    public void setMsg(String s){
        this.mTvMessage.setText(s);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_nowifi;
    }

    @Override
    protected void initViews(Context context) {
        super.initViews(context);
        if (!TextUtils.isEmpty(msg)) {
            mTvMessage.setText(msg);
        }
    }
}
