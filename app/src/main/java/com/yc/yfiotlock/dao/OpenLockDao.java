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
    Completable insertOpenLockInfo(OpenLockInfo openLockInfo);

    @Delete
    Completable deleteOpenLockInfo(OpenLockInfo openLockInfo);

    @Query("update open_lock_info set is_sync=:isSync where lock_id=:lockId and key_id=:keyid")
    Completable updateOpenLockInfo(int lockId, int keyid, boolean isSync);

    @Query("SELECT * FROM open_lock_info where lock_id=:lockId and type=:type and group_type =:groupType")
    Flowable<List<OpenLockInfo>> loadOpenLockInfos(int lockId, int type, int groupType);
}
