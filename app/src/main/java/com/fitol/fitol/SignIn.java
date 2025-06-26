package com.fitol.fitol;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fitol.FitOl.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignIn extends AppCompatActivity {

    ImageView eye;
    EditText password;
    EditText email;

    Boolean eyeControl = false; //göz kapali

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eye = findViewById(R.id.eyeRegister);
        password = findViewById(R.id.register_password);
        email = findViewById(R.id.register_email);

        auth = FirebaseAuth.getInstance();


    }

    public void imageClicked(View view) {
        if (eyeControl)
        {
            eyeControl = false; //göz kapali
            password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //texti password yapar
            eye.setImageResource(R.drawable.visibilty_off);
        }
        else if (!eyeControl)
        {
            eyeControl = true; //göz açik
            password.setTransformationMethod(null); //Texti password yapmaz Text yapar
            eye.setImageResource(R.drawable.visibility_on);
        }
        password.setSelection(password.length()); // Fare en sona gelir
    }

    public void signInButton(View view)
    {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if(emailText.isEmpty() || passwordText.isEmpty())
        {
            Toast.makeText(this, "Lütfen tüm bilgilerinizi giriniz.", Toast.LENGTH_LONG).show();
        }
        else
        {
            auth.signInWithEmailAndPassword(emailText,passwordText).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(SignIn.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this,MainActivity.class);
                    startActivity(intent);

                    email.setText("");
                    password.setText("");
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMessage;

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Geçersiz E-Posta veya Şifre.";
                        password.requestFocus();
                    }
                    else if (e instanceof FirebaseAuthInvalidUserException) {
                        errorMessage = "Bu e-posta adresine sahip bir hesap bulunamadı.";
                        email.requestFocus();
                    }
                    else if (e instanceof FirebaseAuthEmailException) {
                        errorMessage = "Geçersiz e-posta adresi.";
                        email.requestFocus();
                    }
                    else if (e instanceof FirebaseTooManyRequestsException) {
                        errorMessage = "Çok fazla yanlış giriş yapıldı, lütfen daha sonra tekrar deneyin.";
                    }
                    else if (e instanceof FirebaseNetworkException) {
                        errorMessage = "İnternet bağlantınızı kontrol edin.";
                    }
                    else {
                        errorMessage = "Giriş başarısız: " + e.getLocalizedMessage();
                    }
                    Toast.makeText(SignIn.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void forgotPassword(View view) {
        String emailText = email.getText().toString();

        if (emailText.isEmpty()) {
            Toast.makeText(this, "Lütfen E-Posta adresinizi giriniz.", Toast.LENGTH_LONG).show();
        } else {
            auth.sendPasswordResetEmail(emailText).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(SignIn.this, "Şifre sıfırlama E-Postası gönderildi.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignIn.this, "Şifre sıfırlama E-Postası gönderilirken bir hata ile karşılaşıldı.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}