package com.fitol.fitol;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.fitol.FitOl.R;
import com.fitol.FitOl.databinding.ActivityPersonalInfoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInfo extends AppCompatActivity {

    private ActivityPersonalInfoBinding binding;
    int minAge = 18; //Minimum yas
    int maxAge = 99; //Max yas
    List<Integer> ageList = new ArrayList<>(); //spinenrAge yaşlarını tutacak arraylist


    int minHeight = 140; //Minimum boy
    int maxHeight = 210; //Max boy
    List<Integer> heightList = new ArrayList<>(); //spinnerHeight boylarını tutacak arraylist

    String EPOSTA,username,name,surname,weightText,gender;
    int age = 0;  //default = 0
    int height = 0;
    double weight = 0;

    Boolean isDataSaved;
    double vucutKitleIndeksi;


    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getUid();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPersonalInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent getInfo = getIntent();
        EPOSTA = getInfo.getStringExtra("EPOSTA");


        isDataSaved = false;

        //Filtreleme işlemleri
        binding.nameText.setFilters(new InputFilter[] {
                (source, start, end, dest, dstart, dend) -> {String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

                    if (result.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]*$")) {
                        return null;
                    }
                    return "";
                }
        });
        binding.surnameText.setFilters(new InputFilter[] {
                (source, start, end, dest, dstart, dend) -> {String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

                    if (result.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]*$")) {
                        return null;
                    }
                    return "";
                }
        });
        binding.weightText.setFilters(new InputFilter[] {
                (source, start, end, dest, dstart, dend) -> {String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
                    if (result.matches("^\\d{0,3}(\\.\\d{0,2})?$"))
                    {
                        if (result.length() == 3 && !result.contains("."))
                        {
                            result += ".";
                        }
                        return null;
                    }
                    return "";
                }
        });

        binding.usernameText.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

                    if (result.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ]*$")) {
                        return null;
                    }
                    return "";
                }
        });



        for(int i = minAge; i <= maxAge; i++)  //spinnerAge için olan döngüdür. (18-99)
        {
            ageList.add(i);
        }
        for(int i = minHeight; i <= maxHeight; i ++)  //spinnerHeight  için olan döngüdür. (140-210)
        {
            heightList.add(i);
        }


        //Spinner nesnelerine açılır menü veriyoruz ve içerisini verilerle dolduruyorum.
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageList);
        ageAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.spinnerAge.setAdapter(ageAdapter);

        ArrayAdapter<Integer> heightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, heightList);
        heightAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.spinnerHeight.setAdapter(heightAdapter);



        //Spinner nesnelerinin seçimlerini kontrol edip işlem yapıyorm
        binding.spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = (int) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                height = (int) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        binding.radioGroupGender.setOnCheckedChangeListener((new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == binding.radioMale.getId())
                {
                    gender = "Erkek";

                    binding.radioMale.setBackground(getResources().getDrawable(R.drawable.gender_male_bg));
                    binding.radioFemale.setBackground(null);

                    binding.radioMale.setTextColor(getResources().getColor(R.color.white));
                    binding.radioFemale.setTextColor(getResources().getColor(R.color.black));

                    binding.radioMale.animate().alpha(1f).setDuration(300);
                    binding.radioFemale.animate().alpha(0.5f).setDuration(300);
                }
                else if( i == binding.radioFemale.getId())
                {
                    gender = "Kadın";

                    binding.radioMale.setBackground(null);
                    binding.radioFemale.setBackground(getResources().getDrawable(R.drawable.gender_female_bg));

                    binding.radioMale.setTextColor(getResources().getColor(R.color.black));
                    binding.radioFemale.setTextColor(getResources().getColor(R.color.white));

                    binding.radioMale.animate().alpha(0.5f).setDuration(300);
                    binding.radioFemale.animate().alpha(1f).setDuration(300);
                }
            }
        }));


    }

    @Override
    public void onBackPressed() {
        if (!isDataSaved) {
            user.delete(); // Hesabı sil
        }
        super.onBackPressed(); // Geri çık
    }
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // Kullanıcı Home tuşuna basıp uygulamayı terk ettiğinde çalışır
        if (!isDataSaved) {
            user.delete(); // Hesabı sil
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Uygulama sonlandırıldığında yapılacak işlem
        if (!isDataSaved) {
            user.delete(); // Hesabı sil
        }
    }

    public void vki()
    {
        double heightMeters = height / 100.0;
        vucutKitleIndeksi = weight / (heightMeters * heightMeters);
    }


    public void saveDataButton(View View)
    {
        username = binding.usernameText.getText().toString();
        name = binding.nameText.getText().toString();
        surname = binding.surnameText.getText().toString();
        weightText = binding.weightText.getText().toString();
        //age height ve gender zaten alıyoruz.
        if(!username.isEmpty() && !name.isEmpty() && !surname.isEmpty() && !weightText.isEmpty() && age != 0 && height!= 0 && gender != null && !gender.isEmpty()) //Bilgiler tam ise
        {
            weight = Double.parseDouble(weightText);
            vki(); //vücutKitleIndeksi değişkeninin içi dolduruluyor.

            isDataSaved = true;
            if(isDataSaved)
            {

                database.collection("USERS")
                        .whereEqualTo("username",username)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(), "Bu kullanıcı adı zaten kullanılıyor. Lütfen başka bir Kullanıcı Adı seçiniz!", Toast.LENGTH_LONG).show();
                                    binding.usernameText.setText("");
                                }
                                else
                                {
                                    Map<String, Object> user = new HashMap<>(); //Bir hashmap oluşturdum. Bu sayede kullanıcı verilerini tutabilirim.
                                    user.put("E-Posta", EPOSTA);
                                    user.put("username", username);
                                    user.put("name", name);
                                    user.put("surname", surname);
                                    user.put("weight", weight);
                                    user.put("age", age);
                                    user.put("height", height);
                                    user.put("vki", vucutKitleIndeksi);
                                    user.put("gender", gender);


                                    database.collection("USERS")
                                            .document(userID)
                                            .set(user) //user hashmapindeki verileri firestore aktarımını yaptım.
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getApplicationContext(), "Bilgileriniz başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();



                                                    Intent intent = new Intent(PersonalInfo.this,MainActivity.class);
                                                    intent.putExtra("username", username);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Bilgileriniz kaydedilemedi.",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            }
        }
        else
        {
            Toast.makeText(this, "Lütfen tüm bilgilerinizi eksiksiz giriniz!", Toast.LENGTH_LONG).show();
        }

    }
}