package com.yc.yfiotlock.controller.activitys.user;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.ble.IndexFragment;
import com.yc.yfiotlock.controller.fragments.user.MyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.tv_index)
    TextView mTvIndex;
    @BindView(R.id.tv_mine)
    TextView mTvMine;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private int selectedIndex = -1;
    private BaseFragment[] mContentFragments;

    @Override
    protected void initViews() {
        setFullScreen();
        mContentFragments = new BaseFragment[]{
                new IndexFragment(), new MyFragment()
        };
        onSelected(0);
    }

    private BaseFragment getFragment(int idx) {
        if (idx == -1) return null;
        return mContentFragments[idx];
    }

    private void onSelected(int index) {
        if (selectedIndex == index) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment fragment = getFragment(index);
        BaseFragment currentFragment = getFragment(selectedIndex);
        selectedIndex = index;

        if (currentFragment == null) {
            transaction.add(R.id.fl_content, fragment).commitAllowingStateLoss();
            return;
        }

        if (!fragment.isAdded() && getSupportFragmentManager().findFragmentByTag("" + selectedIndex) == null) {
            transaction.hide(currentFragment).add(R.id.fl_content, fragment, "" + selectedIndex).commitAllowingStateLoss();
        } else {
            transaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
        }
        resetItem();
        setItem(index);
    }

    private void setItem(int index) {
        switch (index) {
            case 0:
                mTvIndex.setTextColor(Color.parseColor("#222222"));
                mTvIndex.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.icon_home_sel), null, null);
                break;
            case 1:
                mTvMine.setTextColor(Color.parseColor("#222222"));
                mTvMine.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.icon_personal_sel), null, null);
                break;
        }
    }

    private void resetItem() {
        mTvIndex.setTextColor(Color.parseColor("#909090"));
        mTvMine.setTextColor(Color.parseColor("#909090"));
        mTvIndex.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(this, R.mipmap.icon_home_default), null, null);
        mTvMine.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(this, R.mipmap.icon_personal_default), null, null);
    }


    @OnClick({R.id.ll_index, R.id.ll_mine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_index:
                onSelected(0);
                break;
            case R.id.ll_mine:
                onSelected(1);
                break;
        }
    }
}