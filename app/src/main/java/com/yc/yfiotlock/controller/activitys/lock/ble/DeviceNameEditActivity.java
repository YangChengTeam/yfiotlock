package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * @author Dullyoung
 */
public class DeviceNameEditActivity extends BaseActivity {


    @BindView(R.id.bnb_title)
    BackNavBar mBnbTitle;
    @BindView(R.id.et_name)
    EditText mEtName;

    private DeviceInfo deviceInfo;
    private DeviceEngin deviceEngin;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_edit_device_name;
    }

    @Override
    protected void initVars() {
        super.initVars();
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        deviceEngin = new DeviceEngin(this);
    }

    @Override
    protected void initViews() {
        mBnbTitle.setBackListener(view -> finish());
        mEtName.setText(deviceInfo.getName());
        mEtName.setSelection(mEtName.getText().length());
        CommonUtil.setEditTextLimit(mEtName, 20, true);
        mEtName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void cloudModifyDeivceName() {
        if (deviceInfo != null && !TextUtils.isEmpty(deviceInfo.getId())) {
            String name = mEtName.getText().toString();
            mLoadingDialog.show("正在修改");
            deviceEngin.updateDeviceInfo(deviceInfo.getId(), name).subscribe(new Subscriber<ResultInfo<String>>() {
                @Override
                public void onCompleted() {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    mLoadingDialog.dismiss();
                }

                @Override
                public void onNext(ResultInfo<String> resultInfo) {
                    if (resultInfo != null && resultInfo.getCode() == 1) {
                        deviceInfo.setName(name);
                        ToastCompat.show(getContext(), "修改成功");
                        finish();
                        EventBus.getDefault().post(deviceInfo);
                        EventBus.getDefault().post(new IndexRefreshEvent());
                    } else {
                        String msg = "更新出错";
                        msg = resultInfo != null && resultInfo.getMsg() != null ? resultInfo.getMsg() : msg;
                        ToastCompat.show(getContext(), msg);
                    }
                }
            });
        }
    }

    @Override
    protected void bindClick() {
        setClick(R.id.stv_sure, () -> {
            cloudModifyDeivceName();
        });
    }
}