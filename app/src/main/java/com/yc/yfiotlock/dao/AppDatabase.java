package com.yc.yfiotlock.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yc.yfiotlock.model.bean.lock.DeviceInfo;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.LogInfo;

@Database(entities = {OpenLockInfo.class, LogInfo.class, DeviceInfo.class}, version = 2, exportSchema = false)
public abstract  class AppDatabase extends RoomDatabase {
    public abstract OpenLockDao openLockDao();
    public abstract LockLogDao lockLogDao();
    public abstract DeviceDao deviceDao();
}
