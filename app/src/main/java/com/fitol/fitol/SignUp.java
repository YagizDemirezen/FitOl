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
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    ImageView eye1;
    ImageView eye2;
    EditText email;
    EditText password1;
    EditText password2;

    Boolean eyeControl1 = false; //göz açık
    Boolean eyeControl2 = false; //göz açık

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eye1 = findViewById(R.id.eyeRegister);
        eye2 = findViewById(R.id.eyeRegister2);
        email = findViewById(R.id.register_email);
        password1 = findViewById(R.id.register_password);
        password2 = findViewById(R.id.register_password2);

        auth = FirebaseAuth.getInstance();



    }


    public void imageClicked1(View view)
    {
        if (eyeControl1)
        {
            eyeControl1 = false; //göz kapali
            password1.setTransformationMethod(PasswordTransformationMethod.getInstance()); //texti password yapar
            eye1.setImageResource(R.drawable.visibilty_off);
        }
        else if (!eyeControl1)
        {
            eyeControl1 = true; //göz açik
            password1.setTransformationMethod(null); //Texti password yapmaz Text yapar
            eye1.setImageResource(R.drawable.visibility_on);
        }
        password1.setSelection(password1.length()); // Fare en sona gelir
    }

    public void imageClicked2(View view)
    {
        if (eyeControl2) {
            eyeControl2 = false; //göz kapalı
            password2.setTransformationMethod(PasswordTransformationMethod.getInstance()); //texti password yapar
            eye2.setImageResource(R.drawable.visibilty_off);
        } else if (!eyeControl2) {
            eyeControl2 = true; //göz açık
            password2.setTransformationMethod(null); //Texti password yapmaz Text yapar
            eye2.setImageResource(R.drawable.visibility_on);
        }
        password2.setSelection(password2.length()); // Fare en sona gelir
    }


    public void continueButton(View view)
    {
        String emailText = email.getText().toString();
        String password1Text = password1.getText().toString();
        String password2Text = password2.getText().toString();

        if(emailText.isEmpty() || password1Text.isEmpty() || password2Text.isEmpty())
        {
            Toast.makeText(this, "Lütfen tüm bilgilerinizi giriniz.", Toast.LENGTH_LONG).show();
        }
        else
        {
            if(!password1Text.equals(password2Text)) //Şifreler uyuşmuyorsa
            {
                Toast.makeText(this, "Şifreleriniz uyuşmuyor!", Toast.LENGTH_LONG).show();
                password1.setText("");
                password2.setText("");
                password1.requestFocus();
            }
            else //Problem yok ise
            {
                auth.createUserWithEmailAndPassword(emailText,password1Text).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignUp.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                        Intent personelintent = new Intent(SignUp.this,PersonalInfo.class);

                        personelintent.putExtra("EPOSTA",emailText);
                        startActivity(personelintent);

                        email.setText("");
                        password1.setText("");
                        password2.setText("");

                        eyeControl1 = false; //göz kapali
                        eye1.setImageResource(R.drawable.visibilty_off);
                        eyeControl2 = false; //göz kapali
                        eye2.setImageResource(R.drawable.visibilty_off);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage;

                        if(e instanceof FirebaseAuthWeakPasswordException)
                        {
                            errorMessage = "Şifreniz çok zayıf! (Şifreniz en az 6 karakterli olmalı)";
                            password1.requestFocus();
                        }
                        else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Geçersiz E-Posta formatı.";
                        }
                        else if (e instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "Girdiğiniz E-Posta zaten kullanılıyor.";
                        }
                        else if (e instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "Bu e-posta adresine sahip bir hesap bulunamadı.";
                        }
                        else if (e instanceof FirebaseAuthEmailException) {
                            errorMessage = "Geçersiz e-posta adresi.";
                        }
                        else if (e instanceof FirebaseNetworkException) {
                            errorMessage = "İnternet bağlantınızı kontrol edin.";
                        }
                        else if (e instanceof FirebaseTooManyRequestsException) {
                            errorMessage = "Çok fazla yanlış deneme yapıldı, lütfen daha sonra tekrar deneyin.";
                        }
                        else
                        {
                            errorMessage = "Kayıt başarısız " + e.getLocalizedMessage();
                            System.out.println(e.getLocalizedMessage());
                        }
                        Toast.makeText(SignUp.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

    }



}