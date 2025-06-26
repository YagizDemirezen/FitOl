package com.fitol.fitol;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fitol.FitOl.R;

import java.util.Calendar;

public class HedefActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private SeekBar seekBar, seekBar2;
    private TextView textProgress, textProgress2;
    private Switch switchHatirlatma;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hedef);

        checkNotificationPermission();

        seekBar = findViewById(R.id.seekBar);
        seekBar2 = findViewById(R.id.seekBar2);
        textProgress = findViewById(R.id.textProgress);
        textProgress2 = findViewById(R.id.textProgress2);
        switchHatirlatma = findViewById(R.id.switchSuHatirlatici);
        preferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        boolean isOn = preferences.getBoolean("su_hatirlatma", false);
        switchHatirlatma.setChecked(isOn);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Maksimum sınır: 50.000
                if (progress > 50000) {
                    progress = 50000;
                    seekBar.setProgress(progress);
                }

                // Sayıyı düzgün formatlıyorum
                textProgress.setText(String.format("%,d Adım", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgress2.setText(progress + " LT");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        switchHatirlatma.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("su_hatirlatma", isChecked);
            editor.apply();

            if (isChecked) {
                ayarlaSuHatirlatmalar();
                Toast.makeText(HedefActivity.this, "Hatırlatıcı açıldı!", Toast.LENGTH_SHORT).show();
            } else {
                iptalEtSuHatirlatmalar();
                Toast.makeText(HedefActivity.this, "Hatırlatıcı kapatıldı!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ayarlaSuHatirlatmalar() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Toast.makeText(this, "Alarm yöneticisi bulunamadı!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Kesin alarm izni verilmedi! Lütfen ayarlardan izin verin.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        float[] saatler = {14.24f, 18.30f,21.30f};

        for (int i = 0; i < saatler.length; i++) {
            try {
                Intent intent = new Intent(this, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                int saat = (int) saatler[i];
                int dakika = Math.round((saatler[i] - saat) * 100);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, saat);
                calendar.set(Calendar.MINUTE, dakika);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Hatırlatma ayarlandı: " + saat + ":" + dakika, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void iptalEtSuHatirlatmalar() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        for (int i = 0; i < 3; i++) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bildirim izni verildi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bildirim izni reddedildi! Hatırlatıcılar çalışmayabilir.", Toast.LENGTH_LONG).show();
            }
        }
    }
}