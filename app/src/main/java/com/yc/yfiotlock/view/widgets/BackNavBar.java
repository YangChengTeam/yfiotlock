package com.yc.yfiotlock.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class BackNavBar extends BaseView {

    @BindView(R.id.tv_title)
    TextView mTitleTV;
    @BindView(R.id.iv_back)
    ImageView mBackIv;
    @BindView(R.id.view_line)
    View viewLine;

    @Override
    public int getLayoutId() {
        return R.layout.navbar_back;
    }

    public BackNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.tab_item);
        CharSequence title = a.getText(R.styleable.tab_item_text);
        if (title != null && mTitleTV != null) {
            mTitleTV.setText(title);
        }
        boolean hindLine = a.getBoolean(R.styleable.tab_item_hindLine, false);
        if (viewLine != null && hindLine) {
            viewLine.setVisibility(GONE);
        }
        CharSequence position = a.getText(R.styleable.tab_item_textPosition);
        if (title != null && mTitleTV != null) {
            if (position != null && position.equals("0x1")) {
                mTitleTV.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            } else {
                mTitleTV.setGravity(Gravity.CENTER);
            }
        }

        if (mBackIv != null) {
            RxView.clicks(mBackIv).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
                if (backListener != null) {
                    backListener.onBack(mBackIv);
                }
            });
        }
    }

    public interface BackListener {
        void onBack(View view);
    }

    private BackListener backListener;

    public void setBackListener(BackListener backListener) {
        this.backListener = backListener;
    }


}
