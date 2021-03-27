package com.yc.yfiotlock.controller.activitys.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.controller.fragments.user.DeviceShareListFragment;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
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

    @Override
    protected int getLayoutId() {
        return R.layout.user_activity_device_share;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setVpAndMi();
    }

    private void setVpAndMi() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new DeviceShareListFragment());
        fragments.add(new DeviceShareListFragment());
        String[] strings = new String[]{"共享", "接受"};
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragments, strings, getSupportFragmentManager(), 1);
        mVpDevice.setAdapter(adapter);

        CommonNavigatorAdapter navigatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.GRAY);
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(strings[index]);
                colorTransitionPagerTitleView.setOnClickListener(view -> mVpDevice.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        };
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdapter(navigatorAdapter);
        mMiTitle.setNavigator(navigator);
        ViewPagerHelper.bind(mMiTitle, mVpDevice);
    }


}