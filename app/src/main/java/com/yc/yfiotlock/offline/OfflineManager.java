package com.yc.yfiotlock.offline;

import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
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

        /*
        Constraints.Builder builder = new Constraints.Builder();
        builder.setRequiredNetworkType(NetworkType.CONNECTED);
        WorkRequest workRequest =
                new PeriodicWorkRequest.Builder(OLTWorker.class, 15, TimeUnit.MINUTES).setConstraints(builder.build())
                        .setInitialDelay(15, TimeUnit.MINUTES)
                        .build();
        List<WorkRequest> requests = new ArrayList<>();
        requests.add(workRequest);
        WorkManager
                .getInstance(context)
                .enqueue(requests);
        */

        // 2. AlarmManager 定时任务  补充方案
        context.startService(new Intent(context, OfflineManagerService.class));
    }
}
