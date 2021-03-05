package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yc.yfiotlock.R;

import butterknife.BindView;

public class LeftNextTextView extends BaseView {

    @BindView(R.id.tv_left_next_name)
    TextView tvTitle;
    @BindView(R.id.tv_left_next_des)
    TextView tvDes;
    @BindView(R.id.view_left_next_line)
    View viewLine;

    public LeftNextTextView(Context context) {
        super(context);
    }

    public LeftNextTextView(Context context, AttributeSet attrs) {
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

    public void setTvDes(String des) {
        if (des != null && tvDes != null) {
            tvDes.setText(des);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_left_next_text;
    }
}
