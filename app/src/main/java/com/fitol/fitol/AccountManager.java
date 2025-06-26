package com.fitol.fitol;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fitol.FitOl.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountManager extends AppCompatActivity {
    public Boolean openedBefore;
    public SharedPreferences sharedPreferences;
    private FirebaseAuth auth;

    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView termstext = findViewById(R.id.termsText);
        TextView privacytext = findViewById(R.id.privacyText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            termstext.setPaintFlags(termstext.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            privacytext.setPaintFlags(privacytext.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        sharedPreferences = getSharedPreferences("openController", MODE_PRIVATE);
        openedBefore = sharedPreferences.getBoolean("openController", false);

        if (!openedBefore)
        {
            showPage1();
        }

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        BootCompletedReceiver.scheduleDailyResetWorker(this);

        if (user != null) {
            Intent intent = new Intent(AccountManager.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ACTIVITY_RECOGNITION);
            } else {
                startService(new Intent(this, StepCounterService.class));
            }
        } else {
            startService(new Intent(this, StepCounterService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, StepCounterService.class));
            } else {
                Toast.makeText(this, "İzin verilmedi.", Toast.LENGTH_SHORT).show();            }
        }
    }

    public void showPage1() {
        Intent intent = new Intent(AccountManager.this, FirstPage.class);
        startActivity(intent);
        finish();
    }

    public void loginMethod(View view) {
        Intent intent = new Intent(AccountManager.this, SignIn.class);
        startActivity(intent);
    }

    public void registerMethod(View view) {
        Intent intent = new Intent(AccountManager.this, SignUp.class);
        startActivity(intent);
    }

    public void terms(View view) {
        Intent intent = new Intent(AccountManager.this, Terms.class);
        startActivity(intent);
    }

    public void privacy(View view) {
        Intent intent = new Intent(AccountManager.this, Privacy.class);
        startActivity(intent);
    }
}
