package com.yc.yfiotlock.controller.activitys.user;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.ble.IndexFragment;
import com.yc.yfiotlock.controller.fragments.user.MyFragment;
import com.yc.yfiotlock.helper.ThreadPoolExecutorImpl;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {


    @BindView(R.id.tv_index)
    TextView mTvIndex;
    @BindView(R.id.tv_mine)
    TextView mTvMine;
    @BindView(R.id.vp_index)
    ViewPager mVpIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private static WeakReference<MainActivity> instance;

    public static WeakReference<MainActivity> getInstance() {
        return instance;
    }

    @Override
    protected void initViews() {
        setFullScreen();
        instance = new WeakReference<>(this);
        setVp();
        onSelected(0);
        ThreadPoolExecutorImpl.getImpl().execute(this::deleteLowerVersionApkFile);
    }

    private void setVp() {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new IndexFragment());
        fragments.add(new MyFragment());
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(fragments, new String[0], getSupportFragmentManager(), 1);
        mVpIndex.setAdapter(pagerAdapter);
        mVpIndex.setOffscreenPageLimit(1);
        mVpIndex.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void onSelected(@IntRange(from = 0) int index) {
        if (index == mVpIndex.getCurrentItem()) {
            return;
        }
        setItem(index);
        mVpIndex.setCurrentItem(index, false);
    }

    private void setAnim(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.1f,
                0.5f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        scaleAnimation.setDuration(500);
        view.setAnimation(scaleAnimation);
        scaleAnimation.start();
    }

    private void setItem(int index) {

        resetItem();
        switch (index) {
            case 0:
                mTvIndex.setTextColor(Color.parseColor("#222222"));
                mTvIndex.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.icon_home_sel), null, null);
                setAnim(mTvIndex);
                break;
            case 1:
                mTvMine.setTextColor(Color.parseColor("#222222"));
                mTvMine.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.icon_personal_sel), null, null);
                setAnim(mTvMine);
                break;
            default:
                break;
        }
    }

    private void resetItem() {
        mTvIndex.clearAnimation();
        mTvMine.clearAnimation();
        mTvIndex.setTextColor(Color.parseColor("#909090"));
        mTvMine.setTextColor(Color.parseColor("#909090"));
        mTvIndex.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(this, R.mipmap.icon_home_default), null, null);
        mTvMine.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(this, R.mipmap.icon_personal_default), null, null);
    }

    @Override
    protected void bindClick() {
        setClick(R.id.ll_index, () -> onSelected(0));
        setClick(R.id.ll_mine, () -> onSelected(1));
    }

    /**
     * check local apk file when start every time
     * if the exist apk file is already installed,then delete it to free storage zoom
     * better way is run on a new thread to not influences performance
     * packageManager.getPackageInfo(pkgName,flag),
     * should use accurate flag instead 0
     * flag-0 may make the packageInfo too large that cause {@link PackageManager} throws PackageManagerDeadException
     */
    private void deleteLowerVersionApkFile() {
        if (getPermissionHelper().justStoragePermission().checkMustPermissions(this)) {
            String fileNameFilter = getContext().getResources().getString(R.string.app_name);
            String path = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            PackageManager packageManager = getPackageManager();
            int versionCode = 0;
            try {
                versionCode = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            File downloadDir = new File(path);
            if (downloadDir.exists()) {
                File[] files = downloadDir.listFiles((dir, name) -> name.contains(fileNameFilter));
                if (files == null) {
                    return;
                }
                for (File file : files) {
                    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(),
                            PackageManager.GET_CONFIGURATIONS);
                    if (packageInfo != null && versionCode > packageInfo.versionCode && file.delete()) {
                        Log.d("aaaa", "deleteLowerVersionApkFile: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }


    /**
     * 按下返回键时 通过ACTION 和 CATEGORY 加Flag {@link Intent#CATEGORY_HOME}返回桌面而不关闭当前activity
     */
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mVpIndex != null && mVpIndex.getAdapter() instanceof ViewPagerAdapter) {
            ViewPagerAdapter adapter = (ViewPagerAdapter) mVpIndex.getAdapter();
            if (adapter.getItem(0) instanceof IndexFragment) {
                IndexFragment indexFragment = (IndexFragment) adapter.getItem(0);
                indexFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}