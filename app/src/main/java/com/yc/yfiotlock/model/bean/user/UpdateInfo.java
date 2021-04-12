package com.yc.yfiotlock.model.bean.user;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/3
 */
public class UpdateInfo implements Serializable {
    private String version;
    @JSONField(name = "version_code")
    private int versionCode;
    @JSONField(name = "down_url")
    private String downUrl;
    private String desc;
    //官网地址
    private String url;
    private String speed;
    private String size;
    private long totalSize;
    private long offsetSize;
    private boolean isDownloading;
    private int downloadStatus;
    private int isMust;

    public int getProgress() {
        if (totalSize == 0) {
            return 0;
        }
        return (int) (offsetSize * 100 / totalSize);
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getOffsetSize() {
        return offsetSize;
    }

    public void setOffsetSize(long offsetSize) {
        this.offsetSize = offsetSize;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getIsMust() {
        return isMust;
    }

    public void setIsMust(int isMust) {
        this.isMust = isMust;
    }
}
