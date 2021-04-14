package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.dao.DeviceDao;
import com.yc.yfiotlock.model.bean.eventbus.CloudDeviceEditEvent;
import com.yc.yfiotlock.model.bean.eventbus.IndexRefreshEvent;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.engin.DeviceEngin;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.widgets.BackNavBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;

/**
 * @author Dullyoung
 */
public class DeviceNameEditActivity extends BaseBackActivity {


    @BindView(R.id.et_name)
    EditText mEtName;

    private DeviceInfo deviceInfo;
    private DeviceDao deviceDao;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_edit_device_name;
    }

    @Override
    protected void initVars() {
        super.initVars();
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        deviceDao  = App.getApp().getDb().deviceDao();
    }


    @Override
    protected void initViews() {
        super.initViews();
        mEtName.setText(deviceInfo.getName());
        mEtName.setSelection(mEtName.getText().length());
        CommonUtil.setEditTextLimit(mEtName, 20, true);
        mEtName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @SuppressLint("CheckResult")
    private void localDeviceEdit(){
        String name = mEtName.getText().toString();
        deviceDao.updateDeviceInfo(deviceInfo.getMacAddress(), name).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                deviceInfo.setName(name);
                EventBus.getDefault().post(deviceInfo);
                EventBus.getDefault().post(new IndexRefreshEvent());
                EventBus.getDefault().post(new CloudDeviceEditEvent(deviceInfo));
                finish();
            }
        });
    }

    @Override
    protected void bindClick() {
        setClick(R.id.tv_sure, this::localDeviceEdit);
    }
}