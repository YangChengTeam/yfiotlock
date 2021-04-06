package com.yc.yfiotlock.offline;

import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

public class OfflineManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        OLTOfflineManager.getInstance(context).doTask();
        context.startService(new Intent(context, OfflineManagerService.class));
    }
}
