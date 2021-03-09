package com.yc.yfiotlock.controller.activitys.user;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.fragments.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.ble.IndexFragment;
import com.yc.yfiotlock.controller.fragments.user.MyFragment;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
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

    private static WeakReference<MainActivity> instance;

    public static WeakReference<MainActivity> getInstance() {
        return instance;
    }

    @Override
    protected void initViews() {
        setFullScreen();
        instance = new WeakReference<>(this);
        mContentFragments = new BaseFragment[]{
                new IndexFragment(), new MyFragment()
        };
        onSelected(0);
        new Thread(this::deleteLowerVersionApkFile).start();
    }


    private BaseFragment getFragment(@IntRange(from = 0) int idx) {
        return mContentFragments[idx];
    }

    private void onSelected(@IntRange(from = 0) int index) {
        if (selectedIndex == index) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment fragment = getFragment(index);

        if (selectedIndex == -1) {
            transaction.add(R.id.fl_content, fragment).commitAllowingStateLoss();
            selectedIndex = index;
            return;
        }
        BaseFragment currentFragment = getFragment(selectedIndex);
        selectedIndex = index;

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
                if (files == null) return;
                for (File file : files) {
                    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(),
                            PackageManager.GET_CONFIGURATIONS);
                    if (packageInfo != null && versionCode > packageInfo.versionCode && file.delete()) {
                        Log.i("aaaa", "deleteLowerVersionApkFile: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

}