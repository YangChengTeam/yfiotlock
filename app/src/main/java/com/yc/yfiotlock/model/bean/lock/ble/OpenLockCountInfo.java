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
        if(fingerprintCount < 0){
            fingerprintCount = 0;
        }
        return fingerprintCount;
    }

    public void setFingerprintCount(int fingerprintCount) {
        this.fingerprintCount = fingerprintCount;
    }

    public int getPasswordCount() {
        if(passwordCount < 0){
            passwordCount = 0;
        }
        return passwordCount;
    }

    public void setPasswordCount(int passwordCount) {
        this.passwordCount = passwordCount;
    }

    public int getCardCount() {
        if(cardCount < 0){
            cardCount = 0;
        }
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }
}
