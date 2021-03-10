package com.yc.yfiotlock.model.bean.lock.ble;

import com.alibaba.fastjson.annotation.JSONField;

public class OpenLockCountInfo {

    @JSONField(name = "finger")
    private int fingerprintCount;
    @JSONField(name = "pwd")
    private int passwordCount;
    @JSONField(name = "nfc")
    private int cardCount;

    public int getFingerprintCount() {
        return fingerprintCount;
    }

    public void setFingerprintCount(int fingerprintCount) {
        this.fingerprintCount = fingerprintCount;
    }

    public int getPasswordCount() {
        return passwordCount;
    }

    public void setPasswordCount(int passwordCount) {
        this.passwordCount = passwordCount;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }
}
