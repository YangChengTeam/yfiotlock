package com.yc.yfiotlock.controller.activitys.lock.remote;


import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.AlarmsFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.LogFragment;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
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

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LockLogActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {

    @BindView(R.id.vp_lock_log)
    ViewPager viewPager;

    @BindView(R.id.mi_title)
    MagicIndicator mMiTitle;

    private DeviceInfo lockInfo;
    private LockBLESend lockBLESend;
    private LockLogDao lockLogDao;
    private OpenLockDao openLockDao;
    private int retryCount = 3;
    private int lastId = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_log;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockLogDao = App.getApp().getDb().lockLogDao();
        openLockDao = App.getApp().getDb().openLockDao();

        lockInfo = LockIndexActivity.getInstance().getLockInfo();

        BleDevice bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBLESend = new LockBLESend(this, bleDevice);
        lockBLESend.setNotifyCallback(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        initViewPager();

        if (lockInfo.isShare()) {
            bleFirstSynclog();
        }
    }

    private void bleFirstSynclog() {
        lockLogDao.getLastEventId(lockInfo.getId()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Integer integer) {
                lastId = integer;
                bleSynclog();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                bleSynclog();
            }
        });
    }


    private void bleSynclog() {
        LockBLEEventCmd.event(getContext(), lastId);
    }

    private void initViewPager() {
        String[] stringArray = getResources().getStringArray(R.array.lock_log_array);
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new LogFragment(lockLogDao, lockInfo));
        fragments.add(new AlarmsFragment(lockLogDao, lockInfo));

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

    private void localAdd(LogInfo logInfo) {
        lockLogDao.insertLogInfo(logInfo).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                lastId++;
                EventBus.getDefault().post(logInfo);
                bleSynclog();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (retryCount-- > 0) {
                    localAdd(logInfo);
                } else {
                    retryCount = 3;
                }
            }
        });
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            if (LockBLEEventCmd.SCMD_NO_NEW_EVENT == lockBLEData.getScmd()) {
                return;
            }
            LogInfo logInfo = new LogInfo();
            logInfo.setLockId(lockInfo.getId());
            int n = 4;
            ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), 0, n));
            logInfo.setEventId(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            logInfo.setKeyid(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            logInfo.setType(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            logInfo.setGroupType(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            int year = 2000 + wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            int month = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            int day = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            int hour = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            int minute = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getOther(), n, n++));
            int second = wrapped.get();

            String time = year + "年" + month + "月" + day + "日 " + hour + "时" + minute + "分" + second + "秒";
            logInfo.setTime(time);

            logInfo.setAddtime(System.currentTimeMillis());
            int logType = 1;

            switch (lockBLEData.getScmd()) {
                case LockBLEEventCmd.SCMD_DOORBELL:
                    logInfo.setName("门铃");
                    break;
                case LockBLEEventCmd.SCMD_OPEN_DOOR_INFO:
                    logInfo.setLogType(logType);
                    openLockDao.getName(lockInfo.getId(), logInfo.getType(), logInfo.getGroupType(), logInfo.getKeyid()).subscribeOn(Schedulers.io()).subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            logInfo.setName(s);
                            localAdd(logInfo);
                        }
                    });
                    return;
                case LockBLEEventCmd.SCMD_LOW_BATTERY:
                    logInfo.setName("低电报警");
                    logType = 2;
                    break;
                case LockBLEEventCmd.SCMD_LOCAL_INIT:
                    logInfo.setName("本地初始化");
                    break;
                case LockBLEEventCmd.SCMD_LOCK_CLOSED:
                    logInfo.setName("门锁锁定");
                    break;
                case LockBLEEventCmd.SCMD_LOCK_UNCLOSED:
                    logInfo.setName("门未锁好");
                    break;
                case LockBLEEventCmd.SCMD_DOOR_UNCLOSED:
                    logInfo.setName("门未关上");
                    break;
                case LockBLEEventCmd.SCMD_AVOID_PRY_ALARM:
                    logInfo.setName("防撬报警");
                    logType = 2;
                    break;
            }
            logInfo.setLogType(logType);
            localAdd(logInfo);
        }
    }


    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            bleSynclog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBLESend != null) {
            lockBLESend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBLESend != null) {
            lockBLESend.unregisterNotify();
        }
    }

}
