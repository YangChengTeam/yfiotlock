package com.yc.yfiotlock.controller.activitys.lock.ble.add;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.ble.LockInfo;
import com.yc.yfiotlock.utils.CommonUtil;

import butterknife.BindView;

public class ConnectActivity extends BaseBackActivity {
    @BindView(R.id.ll_title)
    LinearLayout mLlTitle;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_secret)
    ImageView mIvSecret;
    @BindView(R.id.stv_next)
    SuperTextView mStvNext;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_add_connect;
    }

    public static void start(Context context, LockInfo lockInfo) {
        Intent intent = new Intent(context, ConnectActivity.class);
        intent.putExtra("info", lockInfo);
        context.startActivity(intent);
    }


    @Override
    protected void initViews() {
        super.initViews();
        BleDevice bleDevice = getIntent().getParcelableExtra("bleDevice");
        setClick(mStvNext, () -> Connect2Activity.start(getContext(), bleDevice));
        backNavBar.setTitle(bleDevice.getName());
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(mIvSecret, () -> CommonUtil.hiddenEditText(mEtPwd, mIvSecret));

    }


}
