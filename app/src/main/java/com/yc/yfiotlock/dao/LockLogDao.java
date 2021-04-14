package com.yc.yfiotlock.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface LockLogDao {
    // 添加一条数据到本地
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertLogInfo(LogInfo logInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertLogInfos(List<LogInfo> logInfos);

    // 获取本地列表数据
    @Query("SELECT * FROM log_info where lock_id=:lockId and log_type=:logType order by event_id desc limit (:page-1)*:pageSize, (:page)*:pageSize")
    Single<List<LogInfo>> loadLogInfos(int lockId, int logType, int page, int pageSize);

    // 获取本地最后的lastid
    @Query("SELECT event_id FROM log_info where lock_id=:lockId order by event_id desc limit 1")
    Single<Integer> getLastEventId(int lockId);

    // 获取需要添加的云端数据
    @Query("SELECT * FROM log_info where lock_id=:lockId and is_add=0")
    Single<List<LogInfo>> loadNeedAddLogInfos(int lockId);

    // 更新已被添加
    @Query("update log_info set is_add=:isAdd where lock_id=:lockId and event_id=:eventId")
    Completable updateAddLogInfo(int lockId, int eventId, boolean isAdd);

    // 删除锁相关的信息
    @Query("delete from log_info where lock_id=:lockId")
    Completable deleteInfoByLockId(int lockId);
}
