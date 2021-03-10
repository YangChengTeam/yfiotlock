package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

public class RightNextTextView extends BaseView {

    @BindView(R.id.tv_right_next_name)
    TextView tvTitle;
    @BindView(R.id.tv_right_next_des)
    TextView tvDes;
    @BindView(R.id.view_right_next_line)
    View viewLine;

    public RightNextTextView(Context context) {
        super(context);
    }

    public RightNextTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.tab_item);
        CharSequence title = a.getText(R.styleable.tab_item_text);
        if (title != null && tvTitle != null) {
            tvTitle.setText(title);
        }
        CharSequence des = a.getText(R.styleable.tab_item_des);
        if (des != null && tvDes != null) {
            tvDes.setText(des);
        }
        boolean hindLine = a.getBoolean(R.styleable.tab_item_hindLine, false);
        if (viewLine != null && hindLine) {
            viewLine.setVisibility(GONE);
        }
    }

    public void setTvTitle(String title) {
        if (title != null && tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setTvDesColor(int color) {
        if (tvDes != null) {
            tvDes.setTextColor(color);
        }
    }

    public void setTvDes(String des) {
        if (des != null && tvDes != null) {
            tvDes.setText(des);
        }
    }

    public String getDesText() {
        if (tvDes != null) {
            return tvDes.getText().toString().trim();
        }
        return "";
    }

    public void setTvDes(String des, int color) {
        if (des != null && tvDes != null) {
            tvDes.setText(des);
            tvDes.setTextColor(color);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_right_next_text;
    }
}
