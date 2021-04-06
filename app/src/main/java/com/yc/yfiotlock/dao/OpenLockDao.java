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


@Dao
public interface OpenLockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpenLockInfos(List<OpenLockInfo> openLockInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpenLockInfo(OpenLockInfo openLockInfo);

    @Query("update open_lock_info set is_delete=1 where lock_id=:lockId and key_id=:keyid")
    Completable deleteOpenLockInfo(int lockId, int keyid);

    @Query("update open_lock_info set is_add=:isAdd where lock_id=:lockId and key_id=:keyid")
    Completable updateOpenLockInfo(int lockId, int keyid, boolean isAdd);

    @Query("SELECT * FROM open_lock_info where lock_id=:lockId and type=:type and is_delete=0 and group_type =:groupType order by id desc")
    Flowable<List<OpenLockInfo>> loadOpenLockInfos(int lockId, int type, int groupType);


    @Query("SELECT * FROM open_lock_info where lock_id=:lockId and is_add=0 and group_type=:groupType")
    Flowable<List<OpenLockInfo>> loadNeedAddOpenLockInfos(int lockId, int groupType);

    @Query("SELECT * FROM open_lock_info where lock_id=:lockId and is_delete=1 and group_type=:groupType")
    Flowable<List<OpenLockInfo>> loadNeedDelOpenLockInfos(int lockId, int groupType);



}
