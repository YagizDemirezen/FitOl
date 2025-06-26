package com.fitol.fitol;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.fitol.FitOl.R;

public class HomeFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private ProgressBar progressBar, calorieProgressBar;
    private TextView stepCountText, calorieText, targetStepText, targetCalorieText;

    private int stepCount = 0;
    private int initialStepCount = 0;
    private int currentCaloriesBurned = 0;

    private SharedPreferences stepPreferences;
    private SharedPreferences hedefPreferences;

    private int adimHedef = 10000;
    private int kaloriHedef = 500;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(Color.BLACK);
        }

        stepPreferences = requireActivity().getSharedPreferences("StepPreferences", Context.MODE_PRIVATE);
        hedefPreferences = requireActivity().getSharedPreferences("hedef_verileri", Context.MODE_PRIVATE);

        adimHedef = hedefPreferences.getInt("adim_hedef", 10000);
        kaloriHedef = hedefPreferences.getInt("kalori_hedef", 500);

        progressBar = view.findViewById(R.id.progressBar);
        calorieProgressBar = view.findViewById(R.id.calorieProgressBar);
        stepCountText = view.findViewById(R.id.stepCountText);
        calorieText = view.findViewById(R.id.calorieText);
        targetStepText = view.findViewById(R.id.targetStepText);
        targetCalorieText = view.findViewById(R.id.targetCalorieText);

        progressBar.setMax(adimHedef);
        calorieProgressBar.setMax(kaloriHedef);

        if (targetStepText != null) targetStepText.setText("Adım Hedefi: " + adimHedef);
        if (targetCalorieText != null) targetCalorieText.setText("Kalori Hedefi: " + kaloriHedef + " kcal");

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        if (stepSensor == null) {
            Toast.makeText(getContext(), "Adım sensörü bulunamadı", Toast.LENGTH_SHORT).show();
        }

        // İzin kontrolü
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE_HEALTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE_HEALTH}, 2);
            }
        }

        // Servisi başlatıyorum
        Intent serviceIntent = new Intent(getActivity(), StepCounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
        } else {
            requireActivity().startService(serviceIntent);
        }

        loadSavedData(); // Kaydedilen verileri yükle

        return view;
    }

    private void loadSavedData() {
        stepCount = stepPreferences.getInt("stepCount", 0);
        initialStepCount = stepPreferences.getInt("initialStepCount", 0);
        currentCaloriesBurned = stepCount / 20; // Kalori hesabı

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Hedefleri tekrar yükle
        adimHedef = hedefPreferences.getInt("adim_hedef", 10000);
        kaloriHedef = hedefPreferences.getInt("kalori_hedef", 500);

        progressBar.setMax(adimHedef);
        calorieProgressBar.setMax(kaloriHedef);

        if (targetStepText != null) targetStepText.setText("Adım Hedefi: " + adimHedef);
        if (targetCalorieText != null) targetCalorieText.setText("Kalori Hedefi: " + kaloriHedef + " kcal");

        if (stepSensor != null && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }

        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            float rawStepCount = event.values[0];

            if (!stepPreferences.contains("initialStepCount")) {
                initialStepCount = (int) rawStepCount;
                stepPreferences.edit().putInt("initialStepCount", initialStepCount).apply();
            } else {
                initialStepCount = stepPreferences.getInt("initialStepCount", (int) rawStepCount);
            }

            stepCount = (int) rawStepCount - initialStepCount;
            stepCount = Math.max(0, stepCount);
            currentCaloriesBurned = stepCount / 20; // Kalori hesabı

            stepPreferences.edit().putInt("stepCount", stepCount).apply();
            updateUI();
            checkAndShowNotifications(stepCount, currentCaloriesBurned); // Sensör değiştiğinde kontrol et
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void updateUI() {
        progressBar.setProgress(Math.min(stepCount, adimHedef));
        stepCountText.setText(String.valueOf(stepCount));

        calorieProgressBar.setProgress(Math.min(currentCaloriesBurned, kaloriHedef));
        calorieText.setText("Yakılan Kalori: " + currentCaloriesBurned + " kcal");
    }

    private void checkAndShowNotifications(int currentStepCount, int currentCaloriesBurned) {
        boolean adimBildirimGosterildi = hedefPreferences.getBoolean("adimBildirimGosterildi", false);
        boolean kaloriBildirimGosterildi = hedefPreferences.getBoolean("kaloriBildirimGosterildi", false);

        if (currentStepCount >= adimHedef && !adimBildirimGosterildi) {
            gosterBildirim("Adım Hedefine Ulaşıldı!", "Tebrikler! Günlük adım hedefinize ulaştınız.");
            hedefPreferences.edit().putBoolean("adimBildirimGosterildi", true).apply();
        }

        if (currentCaloriesBurned >= kaloriHedef && !kaloriBildirimGosterildi) {
            gosterBildirim("Kalori Hedefine Ulaşıldı!", "Harika! Günlük kalori hedefinize ulaştınız.");
            hedefPreferences.edit().putBoolean("kaloriBildirimGosterildi", true).apply();
        }
    }

    private void gosterBildirim(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "hedef_bildirim_kanali";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Hedef Bildirimleri", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.fitollogo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 200, 200, 300};
            vibrator.vibrate(pattern, -1);
        }

        notificationManager.notify(1, builder.build());
    }
}