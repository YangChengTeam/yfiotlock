package com.yc.yfiotlock.controller.activitys.lock.ble;

import com.kk.securityhttp.domain.ResultInfo;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.dialogs.lock.ble.AlarmOpenLockManagerDialog;
import com.yc.yfiotlock.model.bean.eventbus.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.model.engin.LockEngine;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

public class AlarmOpenLockManagerActivity extends OpenLockManagerActivity {

    private LockEngine lockEngine;


    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_alarm_open_lock_manager;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockEngine = new LockEngine(this);
        LockBLEManager.GROUP_TYPE = LockBLEManager.GROUP_HIJACK;
    }

    @Override
    protected void initViews() {
        super.initViews();
        loadData();

        boolean isShow = MMKV.defaultMMKV().getBoolean("AlarmOpenLockManagerDialog", false);
        if (!isShow) {
            AlarmOpenLockManagerDialog dialog = new AlarmOpenLockManagerDialog(this);
            dialog.show();
            MMKV.defaultMMKV().putBoolean("AlarmOpenLockManagerDialog", true);
        }

    }

    // 开门方式数量
    @Override
    protected void loadData() {
        int type = LockBLEManager.ALARM_TYPE;
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            notifyAdapter(countInfo);
        }
        lockEngine.getOpenLockInfoCount(lockInfo.getId() + "", type + "").subscribe(new Subscriber<ResultInfo<OpenLockCountInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<OpenLockCountInfo> openLockCountInfoResultInfo) {
                if (openLockCountInfoResultInfo.getCode() == 1 && openLockCountInfoResultInfo.getData() != null) {
                    OpenLockCountInfo countInfo = openLockCountInfoResultInfo.getData();
                    notifyAdapter(countInfo);
                    CacheUtil.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
                }
            }
        });
    }


    private void notifyAdapter(OpenLockCountInfo countInfo) {
        int fingerprintCount = 0;
        int passwordCount = 0;
        int cardCount = 0;

        fingerprintCount = countInfo.getFingerprintCount();
        passwordCount = countInfo.getPasswordCount();
        cardCount = countInfo.getCardCount();

        List<OpenLockTypeInfo> openLockTypeInfos = new ArrayList<>();

        OpenLockTypeInfo fingerprintOpenLockTypeInfo = new OpenLockTypeInfo();
        fingerprintOpenLockTypeInfo.setIcon(R.mipmap.icon_fingerprint);
        fingerprintOpenLockTypeInfo.setName("指纹");
        fingerprintOpenLockTypeInfo.setDesp(fingerprintCount + "个指纹");
        openLockTypeInfos.add(fingerprintOpenLockTypeInfo);

        OpenLockTypeInfo passwordOpenLockTypeInfo = new OpenLockTypeInfo();
        passwordOpenLockTypeInfo.setIcon(R.mipmap.icon_serct);
        passwordOpenLockTypeInfo.setName("密码");
        passwordOpenLockTypeInfo.setDesp(passwordCount + "个密码");
        openLockTypeInfos.add(passwordOpenLockTypeInfo);

        OpenLockTypeInfo cardOpenLockTypeInfo = new OpenLockTypeInfo();
        cardOpenLockTypeInfo.setIcon(R.mipmap.icon_nfc);
        cardOpenLockTypeInfo.setName("NFC门卡");
        cardOpenLockTypeInfo.setDesp(cardCount + "个门卡");
        openLockTypeInfos.add(cardOpenLockTypeInfo);

        openLockAdapter.setNewInstance(openLockTypeInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(OpenLockRefreshEvent object) {
        loadData();
    }


}
