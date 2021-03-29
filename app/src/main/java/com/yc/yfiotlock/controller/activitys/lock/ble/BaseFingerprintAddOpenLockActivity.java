package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.content.Intent;

import com.kk.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLEOpCmd;

public abstract class BaseFingerprintAddOpenLockActivity extends BaseAddOpenLockActivity {
    @Override
    protected void initVars() {
        super.initVars();
        this.mcmd = (byte) 0x02;
        this.scmd = (byte) 0x08;
        lockBleSend.setMcmd(mcmd);
        lockBleSend.setScmd(scmd);
    }

    // fuck code
    @Override
    protected void cloudAddSucc() { }
    // fuck code
    @Override
    protected void cloudAdd(int keyid) { }


}
