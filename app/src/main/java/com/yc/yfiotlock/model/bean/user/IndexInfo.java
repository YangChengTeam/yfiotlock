package com.yc.yfiotlock.model.bean.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.FamilyInfo;

import java.util.List;

public class IndexInfo {
    @JSONField(name = "family")
    private FamilyInfo familyInfo;
    @JSONField(name = "locker")
    private List<DeviceInfo> deviceInfos;

    public FamilyInfo getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(FamilyInfo familyInfo) {
        this.familyInfo = familyInfo;
    }

    public List<DeviceInfo> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(List<DeviceInfo> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }
}
