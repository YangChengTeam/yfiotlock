package com.yc.yfiotlock.controller.activitys.lock.remote;


import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.controller.fragments.remote.AlarmsFragment;
import com.yc.yfiotlock.controller.fragments.remote.LogFragment;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LockLogActivity extends BaseActivity {
    @BindView(R.id.tab_lock_log)
    TabLayout tabLayout;
    @BindView(R.id.vp_lock_log)
    ViewPager viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_log;
    }

    @Override
    protected void initViews() {
        initViewPager();
    }

    private void initViewPager() {
        String[] stringArray = getResources().getStringArray(R.array.lock_log_array);
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new LogFragment());
        fragments.add(new AlarmsFragment());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, stringArray, getSupportFragmentManager(), 1);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
