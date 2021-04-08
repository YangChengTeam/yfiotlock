package com.yc.yfiotlock.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


@Dao
public interface OpenLockDao {
    // 同步云端到本地
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpenLockInfos(List<OpenLockInfo> openLockInfo);

    // 添加一条数据到本地
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpenLockInfo(OpenLockInfo openLockInfo);

    // 获取名称
    @Query("SELECT name FROM open_lock_info where lock_id=:lockId and type=:type and is_delete=0 and group_type =:groupType and key_id=:keyid")
    Single<String> getName(int lockId, int type, int groupType, int keyid);

    // 删除类型本地数据
    @Query("delete from open_lock_info where lock_id=:lockId and type=:type and group_type=:groupType and is_add=1 and is_delete=0")
    Completable deleteOpenLockInfos(int lockId, int type, int groupType);

    // 删除一条本地数据
    @Query("delete from open_lock_info where lock_id=:lockId and key_id=:keyid and is_delete=1")
    Completable realDeleteOpenLockInfo(int lockId, int keyid);

    // 更新本地已删除
    @Query("update open_lock_info set is_delete=1 where lock_id=:lockId and key_id=:keyid")
    Completable deleteOpenLockInfo(int lockId, int keyid);

    // 添加已同步到云端
    @Query("update open_lock_info set is_add=:isAdd where lock_id=:lockId and key_id=:keyid")
    Completable updateAddOpenLockInfo(int lockId, int keyid, boolean isAdd);

    // 更新已同步到云端
    @Query("update open_lock_info set is_update=0 where lock_id=:lockId and key_id=:keyid")
    Completable updateOpenLockInfo(int lockId, int keyid);

    // 本地修改名称
    @Query("update open_lock_info set name=:name,is_update=1 where lock_id=:lockId and key_id=:keyid")
    Completable updateOpenLockInfo(int lockId, int keyid, String name);

    // 获取本地列表数据
    @Query("SELECT * FROM open_lock_info where master_lock_id=:lockId and type=:type and group_type =:groupType order by id desc")
    Single<List<OpenLockInfo>> loadOpenLockInfos(int lockId, int type, int groupType);

    // 获取需要同步到云端的数据
    @Query("SELECT * FROM open_lock_info where master_lock_id=:lockId and is_add=0 and group_type=:groupType")
    Single<List<OpenLockInfo>> loadNeedAddOpenLockInfos(int lockId, int groupType);

    // 获取需要删除的云端数据
    @Query("SELECT * FROM open_lock_info where master_lock_id=:lockId and is_delete=1 and group_type=:groupType")
    Single<List<OpenLockInfo>> loadNeedDelOpenLockInfos(int lockId, int groupType);

    // 获取需要更新的云端数据
    @Query("SELECT * FROM open_lock_info where master_lock_id=:lockId and is_update=1 and group_type=:groupType")
    Single<List<OpenLockInfo>> loadNeedUpdateOpenLockInfos(int lockId, int groupType);

}
