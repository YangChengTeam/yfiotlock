package com.yc.yfiotlock.controller.activitys.lock.remote;


import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.AlarmsFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.LogFragment;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;
import com.yc.yfiotlock.view.widgets.BackNavBar;
import com.yc.yfiotlock.view.widgets.VaryingTextSizeTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockLogActivity extends BaseBackActivity {

    @BindView(R.id.vp_lock_log)
    ViewPager viewPager;

    @BindView(R.id.mi_title)
    MagicIndicator mMiTitle;

    private DeviceInfo lockInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_log;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockInfo = LockIndexActivity.getInstance().getLockInfo();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initViewPager();
    }

    private void initViewPager() {

        String[] stringArray = getResources().getStringArray(R.array.lock_log_array);
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new LogFragment(lockInfo));
        fragments.add(new AlarmsFragment(lockInfo));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getSupportFragmentManager(), 1);
        viewPager.setAdapter(viewPagerAdapter);

        CommonNavigatorAdapter navigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                VaryingTextSizeTitleView titleView = new VaryingTextSizeTitleView(context);
                titleView.setNormalColor(0xff222222);
                titleView.setSelectedColor(0xff3091F8);
                titleView.setText(stringArray[index]);
                int dp10 = ScreenUtil.dip2px(getContext(), 20);
                titleView.setPadding(dp10, 0, dp10, 0);
                titleView.setBackgroundResource(R.drawable.ripple_bg);
                titleView.setSelectTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                titleView.setDeSelectTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                titleView.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_bg));
                titleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 3));
                indicator.setLineWidth(UIUtil.dip2px(context, 14));
                indicator.setRoundRadius(UIUtil.dip2px(context, 1.44));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(0xff3395FD);
                indicator.setYOffset(ScreenUtil.dip2px(getContext(), 10));
                return indicator;
            }
        };
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdjustMode(false);
        navigator.setAdapter(navigatorAdapter);
        mMiTitle.setNavigator(navigator);
        ViewPagerHelper.bind(mMiTitle, viewPager);

    }

}
