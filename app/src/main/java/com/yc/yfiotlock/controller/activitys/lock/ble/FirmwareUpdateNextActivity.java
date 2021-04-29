package com.yc.yfiotlock.controller.activitys.lock.ble;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.ble.LockBLEData;
import com.yc.yfiotlock.ble.LockBLEEventCmd;
import com.yc.yfiotlock.ble.LockBLEManager;
import com.yc.yfiotlock.ble.LockBLESender;
import com.yc.yfiotlock.ble.LockBLESettingCmd;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseBackActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.download.DeviceDownloadManager;
import com.yc.yfiotlock.libs.fastble.data.BleDevice;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.AnimatinUtil;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.view.widgets.CircularProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import butterknife.BindView;

public class FirmwareUpdateNextActivity extends BaseBackActivity implements LockBLESender.NotifyCallback {
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

    @BindView(R.id.tv_progress_desp)
    TextView processDespTv;

    @BindView(R.id.tv_version)
    TextView versionTv;
    @BindView(R.id.tv_version_update)
    TextView updateVersionTv;
    @BindView(R.id.tv_new_version)
    TextView newVersionTv;
    @BindView(R.id.tv_desp)
    TextView despTv;

    @BindView(R.id.ll_update_desc)
    View updateDescView;

    @BindView(R.id.stv_update)
    SuperTextView updateBtn;

    private int count = 0;

    private UpdateInfo updateInfo;
    private DeviceInfo deviceInfo;
    private LockBLESender lockBleSender;
    private FileInputStream in;
    private int packageCount;
    private static final int DATA_LENGTH = 200;
    private boolean isUpdating = false;

    @Override
    protected int getLayoutId() {
        return R.layout.lock_ble_activity_firmware_update_next;
    }

    @Override
    protected void initVars() {
        super.initVars();
        deviceInfo = LockIndexActivity.getInstance().getLockInfo();
        updateInfo = (UpdateInfo) getIntent().getSerializableExtra("updateInfo");
        BleDevice bleDevice = LockIndexActivity.getInstance().getBleDevice();
        lockBleSender = new LockBLESender(this, bleDevice, deviceInfo.getKey());
        DeviceDownloadManager.getInstance().init(new WeakReference<>(this));
    }

    @Override
    protected void initViews() {
        super.initViews();
        setInfo();
        DeviceDownloadManager.getInstance().updateApp(updateInfo);
    }

    @Override
    protected void bindClick() {
        super.bindClick();
        setClick(R.id.stv_update, () -> {
            updateBtn.setVisibility(View.GONE);
            DeviceDownloadManager.getInstance().updateApp(updateInfo);
        });
    }

    private void setInfo() {
        despTv.setText(updateInfo.getDesc());
        versionTv.setText(Html.fromHtml("升级" + "(<font color='#999999'>" + updateInfo.getVersion() + "</font>)"));
        newVersionTv.setText("最新版本：" + updateInfo.getVersion());
        updateVersionTv.setText("当前版本：" + deviceInfo.getFirmwareVersion());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(UpdateInfo updateInfo) {
        processDespTv.setText("正在下载");
        circularProgressBar.setProgress(updateInfo.getProgress());
        progressTv.setText(updateInfo.getProgress() + "%");
        processView.setVisibility(View.VISIBLE);
        installView.setVisibility(View.GONE);
        updateSuccessView.setVisibility(View.GONE);
        updateBtn.setVisibility(View.GONE);
        if (updateInfo.getProgress() == 100) {
            processDespTv.setText("等待安装");
            if (!LockBLEManager.getInstance().isConnected(LockIndexActivity.getInstance().getBleDevice())) {
                ToastCompat.show(getContext(), "蓝牙未连接");
                return;
            }
            bleOpenUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(this);
            lockBleSender.registerNotify();
        }

        if(!isUpdating){
            if (DeviceDownloadManager.getInstance().isDownloading()) {
                processDespTv.setText("下载中");
                updateBtn.setVisibility(View.GONE);
            } else {
                processDespTv.setText("等待下载");
                updateBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockBleSender != null) {
            lockBleSender.setNotifyCallback(null);
            lockBleSender.unregisterNotify();
        }

        clear();
    }

    private void bleOpenUpdate() {
        if (lockBleSender != null) {
            byte[] bytes = LockBLESettingCmd.openUpdate(deviceInfo.getKey());
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_OPEN_UPDATE, bytes);
        }
    }

    private void bleUpdate(byte[] datas) {
        if (lockBleSender != null) {
            packageCount -= 1;
            byte[] bytes = LockBLESettingCmd.update(deviceInfo.getKey(), datas, (byte) (packageCount));
            lockBleSender.send(LockBLESettingCmd.MCMD, LockBLESettingCmd.SCMD_UPDATE, bytes, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (LockBLEManager.getInstance().isConnected(LockIndexActivity.getInstance().getBleDevice()) && updateSuccessView.getVisibility() != View.VISIBLE) {
            ToastCompat.show(this, "正在升级中, 请谨慎操作, 切勿退出当前界面?");
        } else {
            super.onBackPressed();
        }
    }


    private void reOpenUpdate() {
        GeneralDialog generalDialog = new GeneralDialog(getContext());
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("开启蓝牙升级失败, 是否重试?");
        generalDialog.setOnPositiveClickListener(dialog -> {
            bleOpenUpdate();
        });
        generalDialog.show();
    }

    private void reUpdate() {
        GeneralDialog generalDialog = new GeneralDialog(getContext());
        generalDialog.setTitle("温馨提示");
        generalDialog.setMsg("蓝牙升级失败, 是否重试?");
        generalDialog.setOnPositiveClickListener(dialog -> {
            initBleUpdate();
            bleUpdate();
        });
        generalDialog.show();
    }

    public void initBleUpdate() {
        try {
            File file = DeviceDownloadManager.getInstance().getFile();
            in = new FileInputStream(file);
            int dlen = DATA_LENGTH;
            packageCount = (int) (file.length() / dlen) + ((file.length() % dlen) > 0 ? 1 : 0);
            LogUtil.msg("文件大小:" + file.length() + " 包数量:" + packageCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void bleUpdate() {
        int dlen = DATA_LENGTH;
        byte[] buffer = new byte[dlen];
        int len;
        try {
            len = in.read(buffer);
            VUiKit.postDelayed(30, () -> {
                bleUpdate(Arrays.copyOfRange(buffer, 0, len));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        installIv.clearAnimation();
        DeviceDownloadManager.getInstance().stopTask();
    }

    @Override
    public void onNotifySuccess(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_OPEN_UPDATE) {
            count++;
            if(count == 2){
                count = 0;
                processView.setVisibility(View.GONE);
                updateSuccessView.setVisibility(View.GONE);
                installView.setVisibility(View.VISIBLE);
                AnimatinUtil.rotate(installIv);
                initBleUpdate();
                bleUpdate();
                isUpdating = true;
            } else {
                bleOpenUpdate();
            }
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_UPDATE) {
            bleUpdate();
        } else if (lockBLEData.getMcmd() == LockBLEEventCmd.MCMD && lockBLEData.getScmd() == LockBLEEventCmd.SCMD_UPDATE_SUCCESS) {
            processView.setVisibility(View.GONE);
            installView.setVisibility(View.GONE);
            updateBtn.setVisibility(View.GONE);
            updateDescView.setVisibility(View.GONE);
            updateSuccessView.setVisibility(View.VISIBLE);
            newVersionTv.setText("当前已是最新版本");
            updateVersionTv.setText("当前版本：" + updateInfo.getVersion());
            clear();
            updateInfo.setUpgrade(false);
            DeviceDownloadManager.getInstance().setUpdateInfo(updateInfo);
            CacheUtil.setCache("firmwareVersion", updateInfo.getVersion());
            EventBus.getDefault().post(deviceInfo);
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_CANCEL_OP) {
            lockBleSender.setOpOver(true);
            mLoadingDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onNotifyFailure(LockBLEData lockBLEData) {
        if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_OPEN_UPDATE) {
            reOpenUpdate();
        } else if (lockBLEData.getMcmd() == LockBLESettingCmd.MCMD && lockBLEData.getScmd() == LockBLESettingCmd.SCMD_UPDATE) {
            isUpdating = false;
            reUpdate();
        }
    }
}
