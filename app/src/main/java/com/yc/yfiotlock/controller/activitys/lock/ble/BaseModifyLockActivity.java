package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.View;
import android.widget.EditText;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;

import butterknife.BindView;

public abstract class BaseModifyLockActivity extends BaseBackActivity {

    @BindView(R.id.et_name)
    EditText nameEt;
    @BindView(R.id.stv_commit)
    View commitBtn;



    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_baes_modify_open_lock;
    }


}
