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
        this.mcmd = LockBLEOpCmd.MCMD;
        this.scmd = LockBLEOpCmd.SCMD_ADD_PRINTFINGER;
        lockBleSend.setMcmd(mcmd);
        lockBleSend.setScmd(scmd);
    }

    @Override
    protected void initViews() {
        super.initViews();
        title = "指纹";
    }

    // fuck code
    @Override
    protected void localAddSucc() { }
    // fuck code
    @Override
    protected void localAdd(int keyid) { }


}
