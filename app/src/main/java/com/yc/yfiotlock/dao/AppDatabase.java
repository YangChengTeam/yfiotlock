package com.yc.yfiotlock.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;

@Database(entities = {OpenLockInfo.class}, version = 1, exportSchema = false)
public abstract  class AppDatabase extends RoomDatabase {
    public abstract OpenLockDao openLockDao();
}
