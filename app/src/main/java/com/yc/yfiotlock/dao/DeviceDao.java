package com.yc.yfiotlock.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.yc.yfiotlock.model.bean.lock.DeviceInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpenLockInfos(List<DeviceInfo> deviceInfos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertDeviceInfo(DeviceInfo deviceInfo);

    // 获取本地列表数据
    @Query("SELECT * FROM device_info where family_id = :familyId")
    Single<List<DeviceInfo>> loadDeviceInfo(int familyId);

    // 添加已同步到云端
    @Query("update device_info set is_add=1 where mac_address=:macAddress")
    Completable updateAddDeviceInfo(String macAddress);

    // 删除一条本地数据
    @Query("delete from device_info where mac_address=:macAddress and is_delete=1")
    Completable realDeleteDeviceInfo(String macAddress);

    // 更新本地已删除
    @Query("update device_info set is_delete=1 where mac_address=:macAddress")
    Completable deleteDeviceInfo(String macAddress);

    // 本地修改名称
    @Query("update device_info set name=:name,is_update=1 where mac_address=:macAddress")
    Completable updateDeviceInfo(String macAddress, String name);

    // 更新已同步到云端
    @Query("update device_info set is_update=0 where mac_address=:macAddress")
    Completable updateDeviceInfo(String macAddress);


    // 获取需要同步到云端的数据
    @Query("SELECT * FROM device_info where is_add = 0 and is_delete = 0")
    Single<List<DeviceInfo>> loadNeedAddDeviceInfos();

    // 获取需要删除的云端数据
    @Query("SELECT * FROM device_info where is_delete = 1")
    Single<List<DeviceInfo>> loadNeedDelDeviceInfos();

    // 获取需要更新的云端数据
    @Query("SELECT * FROM device_info where is_update = 1")
    Single<List<DeviceInfo>> loadNeedUpdateDeviceInfos();
}


