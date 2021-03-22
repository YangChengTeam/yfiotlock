package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.widget.TextView;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockCountInfo;
import com.yc.yfiotlock.utils.CacheUtil;

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
        int cardCount = 0;
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            cardCount = countInfo.getCardCount();
        }
        cardCount += 1;
        String name = "NFC门卡" + ((cardCount) > 9 ? cardCount + "" : "0" + cardCount);
        nameTv.setText(name);

        VUiKit.postDelayed(1000, () -> {
            bleAddCard();
        });
    }

    private void bleAddCard() {
        this.mcmd = (byte) 0x02;
        this.scmd = (byte) 0x05;
        byte[] bytes = LockBLEOpCmd.addCard(this, LockBLEManager.GROUP_TYPE, number);
        lockBleSend.send(mcmd, scmd, bytes);
        mLoadingDialog.show("指令已下发,按提示音操作");
        VUiKit.postDelayed(15 * 1000, () -> {
            if (!isOpOver) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), "已超时");
            }
        });
    }


    @Override
    protected void cloudAddSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(Config.OPEN_LOCK_LIST_URL + type, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setCardCount(countInfo.getCardCount() + 1);
            CacheUtil.setCache(Config.OPEN_LOCK_LIST_URL + type, countInfo);
        }
    }

    @Override
    protected void cloudAdd(String keyid) {
        cloudAdd(nameTv.getText().toString(), LockBLEManager.OPEN_LOCK_CARD, keyid, "");
    }

}
