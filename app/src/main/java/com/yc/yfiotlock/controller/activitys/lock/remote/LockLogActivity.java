package com.yc.yfiotlock.controller.activitys.lock.remote;


import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.utils.VUiKit;
import com.kk.utils.ScreenUtil;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEPackage;
import com.yc.yfiotlock.ble.LockBLESend;
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
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.CommonUtil;
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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

public class LockLogActivity extends BaseBackActivity implements LockBLESend.NotifyCallback {

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;
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
    private LockEngine lockEngine;
    private int lastId = 1;
    private final int MAC_COUNT = 1;
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
        lockEngine = new LockEngine(this);
        lockInfo = LockIndexActivity.getInstance().getLockInfo();

        BleDevice bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBLESend = new LockBLESend(this, bleDevice, lockInfo.getKey());
    }

    @Override
    protected void initViews() {
        super.initViews();

        initViewPager();
        mSrlRefresh.setColorSchemeColors(0xff3091f8);
        mSrlRefresh.setOnRefreshListener(() -> {
            syncView.setVisibility(View.VISIBLE);
            processView.setVisibility(View.VISIBLE);
            syncTv.setVisibility(View.VISIBLE);
            syncTv.setText("同步中，请稍候...");
            bleSyncLog();
            timeout();
            mSrlRefresh.setEnabled(false);
            mSrlRefresh.setRefreshing(false);
        });

        mSrlRefresh.setEnabled(false);
        bleFirstSynclog();
        timeout();
    }

    private void timeout() {
        VUiKit.postDelayed(1000 * 30, () -> {
            if (CommonUtil.isActivityDestory(this)) return;
            if (lockBLESend.isOpOver()) return;

            if (lockBLESend != null) {
                lockBLESend.setNotifyCallback(null);
                lockBLESend.unregisterNotify();
            }
            syncTv.setText("同步超时");
            bleSyncEnd();
        });
    }

    private void bleFirstSynclog() {
        lockLogDao.getLastEventId(lockInfo.getId()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Integer integer) {
                lastId = integer++;
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
            lockBLESend.send(LockBLEEventCmd.MCMD, LockBLEEventCmd.SCMD_LOG, cmdBytes);
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
                if (syncCount <= 0) {
                    syncCount = MAC_COUNT;
                    EventBus.getDefault().post(new LockLogSyncDataEvent());
                }
                EventBus.getDefault().post(logInfo);
                bleSyncLog();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                bleSyncLog();
            }
        });
    }

    public void test() {
        byte[] data = new byte[]{(byte) 0xAA, (byte) 0x00, (byte) 0x00, (byte) 0x1F, (byte) 0x00, (byte) 0x18, (byte) 0x60, (byte) 0x77, (byte) 0xFE, (byte) 0x12, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x15, (byte) 0x04, (byte) 0x0F, (byte) 0x10, (byte) 0x2D, (byte) 0x30, (byte) 0x76, (byte) 0x6E, (byte) 0xA5, (byte) 0x43, (byte) 0xBB};
        LockBLEData lockBLEData = LockBLEPackage.getData(data);
        onNotifySuccess(lockBLEData);
    }

    private String getDecStr(int n) {
        return n > 9 ? "9" : "0" + n;
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            if (LockBLEEventCmd.SCMD_NO_NEW_EVENT == lockBLEData.getScmd()) {
                syncTv.setText("同步完成");
                bleSyncEnd();
                return;
            }
            if (LockBLEEventCmd.SCMD_FINGERPRINT_INPUT_COUNT == lockBLEData.getScmd()) {
                bleSyncLog();
                return;
            }
            LogInfo logInfo = new LogInfo();
            logInfo.setLockId(lockInfo.getId());
            int n = 4;
            ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), 0, n));
            logInfo.setEventId(wrapped.getInt());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            logInfo.setKeyid(wrapped.get());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            logInfo.setType(wrapped.get());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            logInfo.setGroupType(wrapped.get());

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            int year = wrapped.get();
            year += 2000;

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            int month = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            int day = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            int hour = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            int minute = wrapped.get();

            wrapped = ByteBuffer.wrap(Arrays.copyOfRange(lockBLEData.getExtra(), n, ++n));
            int second = wrapped.get();

            String time = year + "-" + getDecStr(month) + "-" + getDecStr(day) + " " + getDecStr(hour) + ":" + getDecStr(minute) + ":" + getDecStr(second);
            logInfo.setTime(time);

            logInfo.setAddtime(System.currentTimeMillis());
            int logType = 1;

            switch (lockBLEData.getScmd()) {
                case LockBLEEventCmd.SCMD_DOORBELL:
                    break;
                case LockBLEEventCmd.SCMD_OPEN_DOOR_INFO:
                    logInfo.setLogType(logType);
                    localGetOpenTypeName(logInfo);
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
            logInfo.setType(lockBLEData.getScmd() + 2);
            logInfo.setLogType(logType);
            localAdd(logInfo);
        }
    }

    private void localGetOpenTypeName(LogInfo logInfo) {
        openLockDao.getName(lockInfo.getId(), logInfo.getType(), logInfo.getGroupType(), logInfo.getKeyid()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                logInfo.setName(s);
                localAdd(logInfo);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                cloudGetOpenTypeName(logInfo);
            }
        });
    }

    private void cloudGetOpenTypeName(LogInfo logInfo) {
        lockEngine.getLockOpenTypeInfo(lockInfo.getId() + "", logInfo.getType() + "", logInfo.getGroupType() + "", logInfo.getKeyid() + "").subscribe(new Action1<ResultInfo<OpenLockInfo>>() {
            @Override
            public void call(ResultInfo<OpenLockInfo> info) {
                logInfo.setName(info.getData().getName());
                if (info != null && info.getCode() == 1 && info.getData() != null) {
                    logInfo.setName(info.getData().getName());
                } else {
                    logInfo.setName("-");
                }
                localAdd(logInfo);
            }
        });
    }


    private void bleSyncEnd() {
        if (CommonUtil.isActivityDestory(this)) return;
        mSrlRefresh.setEnabled(true);
        EventBus.getDefault().post(new LockLogSyncEndEvent());
        processView.setVisibility(View.GONE);
        AnimatinUtil.heightZero(syncView);
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD) {
            syncTv.setText("同步失败");
            bleSyncEnd();
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
