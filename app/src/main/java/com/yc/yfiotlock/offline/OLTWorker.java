package com.yc.yfiotlock.offline;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class OLTWorker extends Worker {
    public static final String TAG = "OLTWorker";

    public OLTWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork start");
        OLTOfflineManager.getInstance(getApplicationContext()).doTask();
        return Result.success();
    }


}
