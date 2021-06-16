package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.widget.TextView;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEBaseCmd;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;
import com.yc.yfiotlock.compat.ToastCompat;
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
        setTitle("NFC门卡");

        int cardCount = 0;
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            cardCount = countInfo.getCardCount();
        }
        cardCount += 1;
        String name = title + ((cardCount) > 9 ? cardCount + "" : "0" + cardCount);
        nameTv.setText(name);

        bleAddCard();
    }

    private void bleAddCard() {
        this.mcmd = LockBLEOpCmd.MCMD;
        this.scmd = LockBLEOpCmd.SCMD_ADD_CARD;
        byte[] bytes = LockBLEOpCmd.addCard(lockInfo.getKey(), LockBLEManager.GROUP_TYPE, number);
        lockBleSender.send(mcmd, scmd, bytes);
        VUiKit.postDelayed(15 * 1000, () -> {
            if (!lockBleSender.isOpOver()) {
                mLoadingDialog.dismiss();
                ToastCompat.show(getContext(), "操作失败");
                finish();
            }
        });
    }


    @Override
    protected void localAddSucc() {
        OpenLockCountInfo countInfo = CacheUtil.getCache(key, OpenLockCountInfo.class);
        if (countInfo != null) {
            countInfo.setCardCount(countInfo.getCardCount() + 1);
            CacheUtil.setCache(key, countInfo);
        }
    }

    @Override
    public void onBackPressed() {
        if (!lockBleSender.isOpOver()) {
            bleCancelDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void localAdd(int keyid) {
        localAdd(nameTv.getText().toString(), LockBLEManager.OPEN_LOCK_CARD, keyid, "");
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        super.onNotifyFailure(lockBLEData);
        if (lockBLEData.getMcmd() == mcmd && lockBLEData.getScmd() == scmd) {
            if (lockBLEData.getStatus() != LockBLEBaseCmd.STATUS_ERROR) {
                bleCancel();
            }
            ToastCompat.show(getContext(), "卡片添加失败");
            finish();
        }
    }
}
