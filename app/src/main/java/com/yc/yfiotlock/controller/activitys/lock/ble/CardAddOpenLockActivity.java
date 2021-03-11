package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.widget.TextView;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.model.bean.OpenLockRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

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

        cloudAddCard("2");
    }

    private void cloudAddCard(String keyid) {
        int cardCount = 0;
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            cardCount = countInfo.getCardCount();
        }
        cardCount += 1;
        String name = "NFC门卡" + ((cardCount) > 9 ? cardCount + "" : "0" + cardCount);
        nameTv.setText(name);
        cloudAdd(name, LockBLEManager.OPEN_LOCK_CARD, keyid, "");
    }

    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtils.getCache(Config.OPEN_LOCK_LIST_URL, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setCardCount(countInfo.getCardCount() + 1);
            CacheUtils.setCache(Config.OPEN_LOCK_LIST_URL, countInfo);
        }
    }
}
