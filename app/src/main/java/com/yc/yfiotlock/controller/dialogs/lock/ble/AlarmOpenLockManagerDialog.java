package com.yc.yfiotlock.controller.dialogs.lock.ble;

import android.content.Context;
import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.constant.Config;
import com.yc.yfiotlock.controller.dialogs.BaseDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class AlarmOpenLockManagerDialog extends BaseDialog {

    @BindView(R.id.stv_ok)
    View okBtn;

    public AlarmOpenLockManagerDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_dialog_alarm_open_lock_manager;
    }

    @Override
    protected void initViews() {
        RxView.clicks(okBtn).throttleFirst(Config.CLICK_LIMIT, TimeUnit.MILLISECONDS).subscribe(view -> {
           dismiss();
        });
    }
}
