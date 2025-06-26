package com.fitol.fitol;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class StepWorker extends Worker {

    public StepWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // SharedPreferences'e erişim alıyorum
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("StepPreferences", Context.MODE_PRIVATE);
        int stepCount = sharedPreferences.getInt("stepCount", 10000);

        // Adım sayısını güncelleme işlemi yapıyorum
        stepCount++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply();

        return Result.success();
    }
}
