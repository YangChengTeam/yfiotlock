package com.yc.yfiotlock.controller.activitys.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.controller.fragments.user.DeviceReceiveListFragment;
import com.yc.yfiotlock.controller.fragments.user.DeviceShareListFragment;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;

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

/**
 * @author Dullyoung
 */
public class DeviceShareActivity extends BaseBackActivity {

    @BindView(R.id.mi_title)
    MagicIndicator mMiTitle;
    @BindView(R.id.vp_device)
    ViewPager mVpDevice;

    public static void seeAllShare(Context context, int index) {
        Intent intent = new Intent(context, DeviceShareActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_device_share;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setVpAndMi();
        int index = getIntent().getIntExtra("index", 0);
        mVpDevice.setCurrentItem(index);
    }

    private void setVpAndMi() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new DeviceShareListFragment());
        fragments.add(new DeviceReceiveListFragment());
        String[] strings = new String[]{"共享", "接受"};
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragments, getSupportFragmentManager(), 1);
        mVpDevice.setAdapter(adapter);

        CommonNavigatorAdapter navigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(0xff222222);
                colorTransitionPagerTitleView.setSelectedColor(0xff3395FD);
                colorTransitionPagerTitleView.setText(strings[index]);
                colorTransitionPagerTitleView.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_bg));
                colorTransitionPagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                colorTransitionPagerTitleView.setOnClickListener(view -> mVpDevice.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 3));
                indicator.setLineWidth(UIUtil.dip2px(context, 21));
                indicator.setRoundRadius(UIUtil.dip2px(context, 1.44));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(0xff3395FD);
                indicator.setYOffset(ScreenUtil.dip2px(getContext(),2));
                return indicator;
            }
        };
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdjustMode(true);
        navigator.setAdapter(navigatorAdapter);
        mMiTitle.setNavigator(navigator);
        ViewPagerHelper.bind(mMiTitle, mVpDevice);
    }


}