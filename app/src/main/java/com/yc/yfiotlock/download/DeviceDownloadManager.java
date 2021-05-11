package com.yc.yfiotlock.download;

import android.content.Context;
import android.text.TextUtils;

import com.yc.yfiotlock.R;
import com.yc.yfiotlock.compat.ToastCompat;
import com.yc.yfiotlock.model.bean.user.UpdateInfo;
import com.yc.yfiotlock.utils.CacheUtil;
import com.yc.yfiotlock.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DeviceDownloadManager extends AppDownloadManager {

    public static final String TAG = "DeviceDownloadManager";

    public static DeviceDownloadManager instance = new DeviceDownloadManager();

    public static DeviceDownloadManager getInstance() {
        return instance;
    }

    private String mac;

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void init(WeakReference<Context> context, String mac) {
        this.mac = mac;
        super.init(context);
    }

    @Override
    public void setUpdateInfo(UpdateInfo updateInfo) {
        CacheUtil.setCache("downloadUpdateBinCache" + mac, updateInfo);
    }

    @Override
    public UpdateInfo getUpdateInfo() {
        return CacheUtil.getCache("downloadUpdateBinCache" + mac, UpdateInfo.class);
    }

    @Override
    protected String getUpdateFileName(UpdateInfo updateInfo) {
        return getContext().getResources().getString(R.string.app_name) + mac + "device" + updateInfo.getVersion() + ".bin";
    }


    @Override
    public void installSelf(UpdateInfo updateInfo) {
        File file = new File(parentDir, getUpdateFileName(updateInfo));
        String md5 = CommonUtil.file2MD5(file);
        if (md5.equals(updateInfo.getFileMd5())) {
            long offsetSize = getFileSize(file);
            if (offsetSize != 0) {
                updateInfo.setTotalSize(offsetSize);
                updateInfo.setOffsetSize(offsetSize);
                updateInfo.setDownloading(false);

                setUpdateInfo(updateInfo);

                EventBus.getDefault().post(updateInfo);
            }
        } else {
            if (file.delete()) {
                ToastCompat.show(getContext(), "文件校验码不正确, 已删除开始重新下载");
                updateApp(updateInfo);
            }
        }
    }
}
