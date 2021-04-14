package com.yc.yfiotlock.controller.activitys.lock.remote;


import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.activitys.lock.ble.LockIndexActivity;
import com.yc.yfiotlock.controller.fragments.base.BaseFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.AlarmsFragment;
import com.yc.yfiotlock.controller.fragments.lock.remote.LogFragment;
import com.yc.yfiotlock.dao.LockLogDao;
import com.yc.yfiotlock.dao.OpenLockDao;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.eventbus.LockLogSyncDataEvent;
import com.yc.yfiotlock.model.bean.eventbus.LockLogSyncEndEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.view.adapters.ViewPagerAdapter;
import com.yc.yfiotlock.view.widgets.VaryingTextSizeTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
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

    @BindView(R.id.tv_sync)
    TextView syncTv;

    @BindView(R.id.ll_sync)
    View syncView;

    @BindView(R.id.pb_process)
    View processView;

    private DeviceInfo lockInfo;
    private LockBLESend lockBLESend;
    private LockLogDao lockLogDao;
    private OpenLockDao openLockDao;
    private int bleRetryCount = 3;
    private int retryCount = 3;
    private int lastId = 1;
    private final int MAC_COUNT = 30;
    private int syncCount = MAC_COUNT;


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
        lockBLESend = new LockBLESend(this, bleDevice, lockInfo.getKey());
    }

    @Override
    protected void initViews() {
        super.initViews();
        initViewPager();

        bleFirstSynclog();
    }

    private void bleFirstSynclog() {
        lockLogDao.getLastEventId(lockInfo.getId()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Integer integer) {
                lastId = integer;
                bleSyncLog();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                bleSyncLog();
            }
        });
    }


    private void bleSyncLog() {
        byte[] cmdBytes = LockBLEEventCmd.event(lockInfo.getKey(), lastId);
        if (lockBLESend != null) {
            lockBLESend.send(LockBLEEventCmd.MCMD, (byte) LockBLEEventCmd.SCMD_LOG, cmdBytes, false);
        }
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
                syncCount--;
                if (syncCount == 0) {
                    syncCount = MAC_COUNT;
                    EventBus.getDefault().post(new LockLogSyncDataEvent());
                }
                EventBus.getDefault().post(logInfo);
                bleSyncLog();
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
                bleSyncEnd();
                return;
            }
            LogInfo logInfo = new LogInfo();
            logInfo.setLockId(lockInfo.getId());
            int n = 4;
            ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), 0, n));
            logInfo.setEventId(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            logInfo.setKeyid(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            logInfo.setType(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            logInfo.setGroupType(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            int year = 2000 + wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            int month = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            int day = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            int hour = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            int minute = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, n++));
            int second = wrapped.get();

            String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + ":";
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


    private void bleSyncEnd() {
        syncTv.setText("同步完成");
        bleRetryCount = 3;
        processView.setVisibility(View.GONE);
        AnimatinUtil.heightZero(syncView);
        EventBus.getDefault().post(new LockLogSyncEndEvent());
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            if (bleRetryCount-- > 0) {
                bleSyncLog();
            } else {
                bleSyncEnd();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBLESend != null) {
            lockBLESend.setNotifyCallback(this);
            lockBLESend.registerNotify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lockBLESend != null) {
            lockBLESend.setNotifyCallback(null);
            lockBLESend.unregisterNotify();
        }
    }

}
