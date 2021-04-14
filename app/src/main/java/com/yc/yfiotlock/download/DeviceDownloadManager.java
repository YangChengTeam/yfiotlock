package com.yc.yfiotlock.download;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.CacheUtil;

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
    public void installSelf(UpdateInfo updateInfo) {
    }

    @Override
    public void setUpdateInfo(UpdateInfo updateInfo) {
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