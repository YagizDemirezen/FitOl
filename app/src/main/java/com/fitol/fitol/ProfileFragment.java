// ProfileFragment.java
package com.fitol.fitol;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fitol.FitOl.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private Button buttonLogout;
    private Switch switchSuHatirlatici, switchEgzersizHatirlatici;
    private SeekBar seekBarSteps, seekBarCalories;
    private TextView textStepGoal, textCalorieGoal, title;
    private SharedPreferences hedefPreferences;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        checkNotificationPermission();

        // Şeffaf status bar
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);


        hedefPreferences = requireActivity().getSharedPreferences("hedef_verileri", Context.MODE_PRIVATE);

        // View'leri bağladım ----- NEDEN BINDING kullanılmadı?
        switchSuHatirlatici = view.findViewById(R.id.switchSuHatirlatici);
        switchEgzersizHatirlatici = view.findViewById(R.id.switchExerciseReminder);
        seekBarSteps = view.findViewById(R.id.seekBarAdim);
        seekBarCalories = view.findViewById(R.id.seekBarKalori);
        textStepGoal = view.findViewById(R.id.textViewAdimHedefi);
        textCalorieGoal = view.findViewById(R.id.textViewKaloriHedefi);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        title = view.findViewById(R.id.textViewTitle);


        title.setLetterSpacing(0.2f);
        int adimHedef = hedefPreferences.getInt("adim_hedef", 10000);
        int kaloriHedef = hedefPreferences.getInt("kalori_hedef", 5000);

        seekBarSteps.setProgress(adimHedef);
        seekBarCalories.setProgress(kaloriHedef);

        textStepGoal.setText("Adım Hedefi: " + adimHedef);
        textCalorieGoal.setText("Kalori Hedefi: " + kaloriHedef);

        seekBarSteps.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStepGoal.setText("Adım Hedefi: " + progress);
                hedefPreferences.edit().putInt("adim_hedef", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarCalories.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textCalorieGoal.setText("Kalori Hedefi: " + progress);
                hedefPreferences.edit().putInt("kalori_hedef", progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Hatırlatıcı ayarları
        SharedPreferences preferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        switchSuHatirlatici.setChecked(preferences.getBoolean("su_hatirlatici", false));
        switchEgzersizHatirlatici.setChecked(preferences.getBoolean("egzersiz_hatirlatici", false));

        switchSuHatirlatici.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("su_hatirlatici", isChecked).apply();
            if (isChecked) {
                checkNotificationPermission();
                ayarlaSuHatirlatmalar();
                Toast.makeText(requireContext(), "Su hatırlatıcı açıldı!", Toast.LENGTH_SHORT).show();
            } else {
                iptalSuHatirlatmalar();
                Toast.makeText(requireContext(), "Su hatırlatıcı kapatıldı!", Toast.LENGTH_SHORT).show();
            }
        });

        switchEgzersizHatirlatici.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("egzersiz_hatirlatici", isChecked).apply();
            if (isChecked) {
                checkNotificationPermission();
                ayarlaEgzersizHatirlatmalar();
                Toast.makeText(requireContext(), "Egzersiz hatırlatıcı açıldı!", Toast.LENGTH_SHORT).show();
            } else {
                iptalEgzersizHatirlatmalar();
                Toast.makeText(requireContext(), "Egzersiz hatırlatıcı kapatıldı!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        hedefPreferences.edit().clear().apply();
        Intent intent = new Intent(requireActivity(), AccountManager.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finishAffinity();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void ayarlaSuHatirlatmalar() {}
    private void iptalSuHatirlatmalar() {}
    private void ayarlaEgzersizHatirlatmalar() {}
    private void iptalEgzersizHatirlatmalar() {}
}
