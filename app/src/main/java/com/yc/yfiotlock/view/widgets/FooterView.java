package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

/*
 * Created by　Dullyoung on 2021/3/8
 */
public class FooterView extends BaseView {
    @BindView(R.id.tv_message)
    TextView mTvMessage;

    public FooterView(Context context) {
        super(context);
    }

    private String msg;

    public FooterView(Context context, String msg) {
        super(context);
        this.msg = msg;
    }


    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_footer;
    }

    public void setMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            mTvMessage.setText(msg);
        }
    }

    @Override
    protected void initViews(Context context) {
        super.initViews(context);
        String msg = "输入密码开锁时,请以<font color='#2F90F7'>#号结尾</font>";
        mTvMessage.setText(Html.fromHtml(msg));
    }
}
