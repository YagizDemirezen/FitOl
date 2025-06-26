package com.fitol.fitol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResetWorker extends Worker {

    public ResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SharedPreferences stepPreferences = context.getSharedPreferences("StepPreferences", Context.MODE_PRIVATE);
        SharedPreferences hedefPreferences = context.getSharedPreferences("hedef_verileri", Context.MODE_PRIVATE);

        // Adım ve kalori verilerini sıfırlamak için
        SharedPreferences.Editor editor = stepPreferences.edit();
        editor.putInt("stepCount", 0);
        editor.putInt("initialStepCount", 0);
        editor.putString("lastResetDate", getCurrentDate());
        editor.apply();

        // Bildirim durumlarını sıfırlamak için
        SharedPreferences.Editor editorHedef = hedefPreferences.edit();
        editorHedef.putBoolean("adimBildirimGosterildi", false);
        editorHedef.putBoolean("kaloriBildirimGosterildi", false);
        editorHedef.apply();

        // Servisi yeniden başlatıyo
        Intent serviceIntent = new Intent(context, StepCounterService.class);
        context.stopService(serviceIntent);
        context.startService(serviceIntent);

        return Result.success();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}