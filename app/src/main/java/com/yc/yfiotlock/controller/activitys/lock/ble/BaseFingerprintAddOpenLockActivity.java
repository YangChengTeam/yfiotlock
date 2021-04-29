package com.yc.yfiotlock.controller.activitys.lock.ble;

import com.yc.yfiotlock.ble.LockBLEOpCmd;

public abstract class BaseFingerprintAddOpenLockActivity extends BaseAddOpenLockActivity {
    @Override
    protected void initVars() {
        super.initVars();
        this.mcmd = LockBLEOpCmd.MCMD;
        this.scmd = LockBLEOpCmd.SCMD_ADD_PRINTFINGER;
        lockBleSender.setMcmd(mcmd);
        lockBleSender.setScmd(scmd);
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
