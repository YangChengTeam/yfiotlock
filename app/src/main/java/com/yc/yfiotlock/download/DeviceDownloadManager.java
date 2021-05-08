package com.yc.yfiotlock.download;

import android.text.TextUtils;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.CacheUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DeviceDownloadManager extends AppDownloadManager {

    public static final String TAG = "DeviceDownloadManager";

    public static DeviceDownloadManager instance = new DeviceDownloadManager();

    public static DeviceDownloadManager getInstance() {
        return instance;
    }

    @Override
    public void setUpdateInfo(UpdateInfo updateInfo) {
        CacheUtil.setCache("downloadUpdateBinCache", updateInfo);
    }

    @Override
    public UpdateInfo getUpdateInfo() {
        return CacheUtil.getCache("downloadUpdateBinCache", UpdateInfo.class);
    }

    @Override
    protected String getUpdateFileName(UpdateInfo updateInfo) {
        return getContext().getResources().getString(R.string.app_name) + "device" + updateInfo.getVersion() + ".bin";
    }

    @Override
    public void installSelf(UpdateInfo updateInfo) {
        File file = new File(parentDir, getUpdateFileName(updateInfo));
        long offsetSize = getFileSize(file);
        if (offsetSize != 0) {
            updateInfo.setTotalSize(offsetSize);
            updateInfo.setOffsetSize(offsetSize);
            updateInfo.setDownloading(false);

            setUpdateInfo(updateInfo);

            EventBus.getDefault().post(updateInfo);
        }
    }
}
