package com.yc.yfiotlock.view.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yc.yfiotlock.controller.fragments.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * FragmentPagerAdapter适用于少量Fragment ,Fragment对象会一直存留在内存中
 * 如大量fragment 则用{@link androidx.fragment.app.FragmentStatePagerAdapter}
 * 当使用FragmentStatePagerAdapter 时，如果Fragment不显示，那么Fragment对象会被销毁
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;
    private String[] titles;

    public ViewPagerAdapter(List<BaseFragment> fragments, String[] titles, @NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.fragments = fragments;
        this.titles = titles;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
