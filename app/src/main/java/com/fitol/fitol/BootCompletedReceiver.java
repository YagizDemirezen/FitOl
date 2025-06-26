package com.fitol.fitol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            scheduleDailyResetWorker(context);
            // Restart step counter service if needed
            context.startService(new Intent(context, StepCounterService.class));
        }
    }

    public static void scheduleDailyResetWorker(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long currentTime = System.currentTimeMillis();
        long initialDelay = calendar.getTimeInMillis() - currentTime;
        if (initialDelay < 0) {
            initialDelay += TimeUnit.DAYS.toMillis(1);
        }

        PeriodicWorkRequest resetWorkRequest = new PeriodicWorkRequest.Builder(
                ResetWorker.class,
                1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "dailyResetWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                resetWorkRequest
        );
    }
}