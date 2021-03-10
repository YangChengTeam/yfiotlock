package com.yc.yfiotlock.model.bean.lock.ble;

public class OpenLockCountInfo {

    private int fingerprintCount;
    private int passwordCount;
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
