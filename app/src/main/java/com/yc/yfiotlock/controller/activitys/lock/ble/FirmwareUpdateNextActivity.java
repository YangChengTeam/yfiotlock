package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLESend;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.download.DeviceDownloadManager;
import com.yc.yfiotlock.download.DownloadUtils;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.CommonUtil;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import butterknife.BindView;

public class FirmwareUpdateNextActivity extends BaseBackActivity implements DeviceDownloadManager.DataCallback, LockBLESend.NotifyCallback {
    @BindView(R.id.fl_process)
    View processView;
    @BindView(R.id.ll_update_success)
    View updateSuccessView;
    @BindView(R.id.fl_install)
    View installView;
    @BindView(R.id.cpb_progress)
    CircularProgressBar circularProgressBar;
    @BindView(R.id.tv_progress)
    TextView progressTv;
    @BindView(R.id.iv_install)
    ImageView installIv;

    @BindView(R.id.tv_version)
    TextView versionTv;
    @BindView(R.id.tv_version_update)
    TextView updateVersionTv;
    @BindView(R.id.tv_new_version)
    TextView newVersionTv;
    @BindView(R.id.tv_desp)
    TextView despTv;

    private UpdateInfo updateInfo;
    private DeviceInfo deviceInfo;
    private LockBLESend lockBleSend;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_firmware_update_next;
    }

    @Override
    protected void initVars() {
        super.initVars();
        lockBleSend = new LockBLESend(this, LockIndexActivity.getInstance().getBleDevice());
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        updateInfo = (UpdateInfo) getIntent().getSerializableExtra("updateInfo");
        DeviceDownloadManager.getInstance().init(new WeakReference<>(this));
    }

    @Override
    protected void initViews() {
        super.initViews();
        setInfo();
    }

    private void setInfo() {
        despTv.setText(updateInfo.getDesc());
        versionTv.setText(Html.fromHtml("升级" + "(<font color='#999999'>" + updateInfo.getVersion() + "</font>)"));
        newVersionTv.setText("最新版本：" + updateInfo.getVersion());
        updateVersionTv.setText("当前版本：" + deviceInfo.getFirmwareVersion());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(UpdateInfo updateInfo) {
        circularProgressBar.setProgress(updateInfo.getProgress());
        progressTv.setText(updateInfo.getProgress() + "%");
        processView.setVisibility(View.VISIBLE);
        installView.setVisibility(View.GONE);
        updateSuccessView.setVisibility(View.GONE);
        if (updateInfo.getProgress() == 100) {
            processView.setVisibility(View.GONE);
            updateSuccessView.setVisibility(View.GONE);
            installView.setVisibility(View.VISIBLE);
            AnimatinUtil.rotate(installIv);
            bleOpenUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(this);
            lockBleSend.registerNotify();
        }
        DeviceDownloadManager.getInstance().updateApp(updateInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockBleSend != null) {
            lockBleSend.setNotifyCallback(null);
            lockBleSend.unregisterNotify();
        }
        installIv.clearAnimation();
    }

    private void bleOpenUpdate() {
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.openUpdate(this);
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_OPEN_UPDATE, bytes, true);
        }
    }

    private void bleUpdate(byte[] datas) {
        if (lockBleSend != null) {
            byte[] bytes = LockBLESettingCmd.update(this, datas);
            lockBleSend.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_UPDATE, bytes, true);
        }
    }

    @Override
    public void onBackPressed() {
        if (updateSuccessView.getVisibility() != View.VISIBLE) {
            cancelDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void cancelDialog() {
        GeneralDialog generalDialog = new GeneralDialog(getContext());
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("正在升级中,确认取消?");
        generalDialog.setOnPositiveClickListener(dialog -> {
            super.onBackPressed();
        });
        generalDialog.show();
    }

    private void reOpenUpdate() {
        GeneralDialog generalDialog = new GeneralDialog(getContext());
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("开启蓝牙升级失败, 重新开启?");
        generalDialog.setOnPositiveClickListener(dialog -> {
            bleOpenUpdate();
        });
        generalDialog.show();
    }

    @Override
    public void post(byte[] buffer, int len) {
        bleUpdate(Arrays.copyOfRange(buffer, 0, len));
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_OPEN_UPDATE) {
            DeviceDownloadManager.getInstance().postData();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_OPEN_UPDATE) {
            reOpenUpdate();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_UPDATE) {
            ToastCompat.show(this, "蓝牙升级失败");
        }
    }
}
