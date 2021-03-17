package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceNameEditActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.stv_sure)
    SuperTextView mSTvSure;
    @BindView(R.id.et_name)
    EditText mEtName;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_edit_device_name;
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            mEtName.setText(name);
            mEtName.setSelection(name.length());
        }
        mEtName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    @OnClick(R.id.stv_sure)
    public void onViewClicked() {
        finish();
    }
}