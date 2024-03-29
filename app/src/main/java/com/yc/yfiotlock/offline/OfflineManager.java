package com.yc.yfiotlock.offline;

import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OfflineManager {
    public static void enqueue(Context context) {
        // 1. WorkManager 最小间隔15分钟 生命周期更长
        Constraints.Builder builder = new Constraints.Builder();
        builder.setRequiredNetworkType(NetworkType.CONNECTED);
        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(OLTWorker.class, 15, TimeUnit.MINUTES).setConstraints(builder.build())
                        .build();
        WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("lockinfosync", ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }
}
