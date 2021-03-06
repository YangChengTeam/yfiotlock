package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.widget.TextView;

import com.kk.securityhttp.utils.LogUtil;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Random;

import butterknife.BindView;

public class CardAddOpenLockActivity extends BaseAddOpenLockActivity {

    @BindView(R.id.tv_name)
    TextView nameTv;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_card_add_open_lock;
    }

    @Override
    protected void initViews() {
        super.initViews();
        cloudAdd("2");

        LogUtil.msg("+++++++++++++++++++++++" + type);
    }

    private void bleAddCard() {
        this.mcmd = (byte) 0x02;
        this.scmd = (byte) 0x05;
        byte[] bytes = LockBLEOpCmd.addCard(this, LockBLEManager.GROUP_TYPE, number);
        lockBleSend.send(mcmd, scmd, bytes);
    }


    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setCardCount(countInfo.getCardCount() + 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
        }
    }

    @Override
    protected void cloudAdd(String keyid) {
        int cardCount = 0;
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            cardCount = countInfo.getCardCount();
        }
        cardCount += 1;
        String name = "NFC门卡" + ((cardCount) > 9 ? cardCount + "" : "0" + cardCount);
        nameTv.setText(name);
        cloudAdd(name, LockBLEManager.OPEN_LOCK_CARD, keyid, "");
    }
}
