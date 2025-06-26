package com.fitol.fitol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fitol.FitOl.R;

public class StepCounterService extends Service implements SensorEventListener {

    private static final String TAG = "StepCounterService";
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int initialStepCount;
    private static final String CHANNEL_ID = "step_channel";
    private static final int NOTIFICATION_ID = 1;
    private int lastStepCount = 0;
    private int lastCaloriesBurned = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        SharedPreferences sharedPreferences = getSharedPreferences("StepPreferences", MODE_PRIVATE);
        initialStepCount = sharedPreferences.getInt("initialStepCount", 0);
        lastStepCount = sharedPreferences.getInt("stepCount", 0);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepSensor != null) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
                Log.d(TAG, "Step sensor registered");
            } else {
                Log.e(TAG, "Step counter sensor not available");
            }
        }

        startForegroundServiceWithNotification();
    }

    private void startForegroundServiceWithNotification() {
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Adım Takibi Aktif")
                .setContentText("Adımlarınız kaydediliyor")
                .setSmallIcon(R.drawable.fitollogo)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Adım Takibi Servisi",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            SharedPreferences stepPreferences = getSharedPreferences("StepPreferences", MODE_PRIVATE);
            SharedPreferences hedefPreferences = getSharedPreferences("hedef_verileri", MODE_PRIVATE);

            int adimHedef = hedefPreferences.getInt("adim_hedef", 10000);
            int kaloriHedef = hedefPreferences.getInt("kalori_hedef", 500);

            if (initialStepCount == 0) {
                initialStepCount = (int) event.values[0];
                stepPreferences.edit().putInt("initialStepCount", initialStepCount).apply();
                Log.d(TAG, "Initial step count set: " + initialStepCount);
            }

            int currentStepCount = (int) event.values[0] - initialStepCount;
            int currentCaloriesBurned = currentStepCount / 20; // 20 steps = 1 calorie

            // Sadece adım sayısı değişirse kaydetmeliyim bu sayede son adımı gösteririm
            if (currentStepCount != lastStepCount) {
                stepPreferences.edit().putInt("stepCount", currentStepCount).apply();
                lastStepCount = currentStepCount;
                Log.d(TAG, "Step count updated: " + currentStepCount);
            }

            // Hedef kontrolü ve bildirim
            checkAndShowNotifications(currentStepCount, currentCaloriesBurned, adimHedef, kaloriHedef);
        }
    }

    private void checkAndShowNotifications(int currentStepCount, int currentCaloriesBurned, int adimHedef, int kaloriHedef) {
        SharedPreferences hedefPreferences = getSharedPreferences("hedef_verileri", MODE_PRIVATE);
        boolean adimBildirimGosterildi = hedefPreferences.getBoolean("adimBildirimGosterildi", false);
        boolean kaloriBildirimGosterildi = hedefPreferences.getBoolean("kaloriBildirimGosterildi", false);

        Log.d(TAG, "Checking goals - Steps: " + currentStepCount + "/" + adimHedef +
                ", Calories: " + currentCaloriesBurned + "/" + kaloriHedef);

        if (currentStepCount >= adimHedef && !adimBildirimGosterildi) {
            showGoalNotification("Adım Hedefine Ulaşıldı!",
                    "Tebrikler! " + adimHedef + " adım hedefinize ulaştınız. Şu anki adım sayınız: " + currentStepCount);
            hedefPreferences.edit().putBoolean("adimBildirimGosterildi", true).apply();
            Log.d(TAG, "Step goal reached notification shown");
        }

        if (currentCaloriesBurned >= kaloriHedef && !kaloriBildirimGosterildi) {
            showGoalNotification("Kalori Hedefine Ulaşıldı!",
                    "Harika! " + kaloriHedef + " kalori yakma hedefinize ulaştınız. Şu anki yakım: " + currentCaloriesBurned + " kalori");
            hedefPreferences.edit().putBoolean("kaloriBildirimGosterildi", true).apply();
            Log.d(TAG, "Calorie goal reached notification shown");
        }
    }

    private void showGoalNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "goal_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Hedef Bildirimleri",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Hedeflere ulaşıldığında bildirim gösterir");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.fitollogo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .build();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 500, 200, 500},
                        -1
                ));
            } else {
                vibrator.vibrate(new long[]{0, 500, 200, 500}, -1);
            }
        }

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}