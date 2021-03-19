package com.yc.yfiotlock.controller.activitys.lock.remote;


import android.content.Context;
import android.content.Intent;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.AlarmsFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.LogFragment;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LockLogActivity extends BaseActivity {
    @BindView(R.id.tab_lock_log)
    TabLayout tabLayout;
    @BindView(R.id.vp_lock_log)
    ViewPager viewPager;

    private DeviceInfo deviceInfo;

    public static void start(Context context, DeviceInfo deviceInfo) {
        Intent intent = new Intent(context, LockLogActivity.class);
        intent.putExtra("device", deviceInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_log;
    }

    @Override
    protected void initVars() {
        super.initVars();
        Serializable device = getIntent().getSerializableExtra("device");
        if (device instanceof DeviceInfo) {
            this.deviceInfo = (DeviceInfo) device;
        }
    }


    @Override
    protected void initViews() {
        initViewPager();
    }

    private void initViewPager() {
        String[] stringArray = getResources().getStringArray(R.array.lock_log_array);
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new LogFragment(deviceInfo));
        fragments.add(new AlarmsFragment(deviceInfo));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, stringArray, getSupportFragmentManager(), 1);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
