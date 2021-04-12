package com.yc.yfiotlock.download;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kk.securityhttp.utils.LogUtil;
import com.kk.securityhttp.utils.VUiKit;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.tencent.mmkv.MMKV;
import com.yc.yfiotlock.App;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.controller.activitys.base.BaseActivity;
import com.yc.yfiotlock.controller.dialogs.GeneralDialog;
import com.yc.yfiotlock.helper.PermissionHelper;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;


public class DeviceDownloadManager extends AppDownloadManager {

    public static final String TAG = "DeviceDownloadManager";

    @Override
    public void installSelf(UpdateInfo updateInfo) {
    }
    
    @Override
    protected void setUpdateInfo(UpdateInfo updateInfo) {
        CacheUtil.setCache("downloadUpdateHexCache", updateInfo);
    }

    @Override
    public UpdateInfo getUpdateInfo() {
        return CacheUtil.getCache("downloadUpdateHexCache", UpdateInfo.class);
    }

    @Override
    protected String getUpdateFileName(UpdateInfo updateInfo) {
        return getContext().getResources().getString(R.string.app_name) + updateInfo.getVersionCode() + ".hex";
    }
}
