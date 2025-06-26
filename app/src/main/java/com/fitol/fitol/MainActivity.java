package com.fitol.fitol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fitol.FitOl.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNotificationPermission();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.bottom_home) {
                    replaceFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.bottom_search) {
                    replaceFragment(new SearchFragment());
                    return true;
                } else if (itemId == R.id.bottom_add) {
                    showBottomSheetDialog(); // Burada bottom navigation bar açılıyo
                    return true;
                } else if (itemId == R.id.bottom_profile) {
                    replaceFragment(new ProfileFragment());
                    return true;
                } else if (itemId == R.id.bottom_reels) {
                    replaceFragment(new ReelsFragment());
                    return true;
                } else {
                    return false;
                }
            }
        });

        replaceFragment(new HomeFragment());
    }


    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
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
                Toast.makeText(this, "Bildirim izni gerekli! Bildirimler çalışmayabilir.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(view);

        TextView shareOption = view.findViewById(R.id.shareOption);
        TextView addFoodOption = view.findViewById(R.id.addFoodOption);

        shareOption.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            replaceFragment(new ShareFragment());
        });

        addFoodOption.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            replaceFragment(new FoodFragment());
        });

        bottomSheetDialog.show();
    }

}
