package com.fitol.fitol;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fitol.FitOl.databinding.FragmentReelsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

public class ReelsFragment extends Fragment {

    int minAge = 18; //Minimum yas
    int maxAge = 99; //Max yas
    List<Integer> ageList = new ArrayList<>(); //spinenrAge yaşlarını tutacak arraylist


    int minHeight = 140; //Minimum boy
    int maxHeight = 210; //Max boy
    List<Integer> heightList = new ArrayList<>(); //spinnerHeight boylarını tutacak arraylist



    private FragmentReelsBinding binding;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    String username,weightStatus;
    int age,height;
    double weight, vki = 0;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentReelsBinding.inflate(inflater, container, false);


        if (auth.getCurrentUser() != null) {
         userID = auth.getCurrentUser().getUid();
         //Toast.makeText(requireContext(), "userID = " + userID, Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(Color.BLACK); // Siyah yapmak için
        }
        binding.profileTitle2.setLetterSpacing(0.2f); //Başlığın arasına letter spacing ekledikm (aralıklı ve daha güzel gorünüyo)


        binding.weightText2.setFilters(new InputFilter[] {
                (source, start, end, dest, dstart, dend) -> {String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
                    if (result.matches("^\\d{0,3}(\\.\\d{0,2})?$"))
                    {
                        if (result.length() == 3 && !result.contains("."))
                        {
                            result += ".";
                        }
                        return null; // Geçerli giriş
                    }
                    return ""; // Geçersiz giriş
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
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ageList);
        ageAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.ageSpinner.setAdapter(ageAdapter);

        ArrayAdapter<Integer> heightAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, heightList);
        heightAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.heightSpinner.setAdapter(heightAdapter);

        binding.ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = (int) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                height = (int) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        ConnectivityManager internetController = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internet = internetController.getActiveNetworkInfo();

        if (internet != null && internet.isConnected())
        {
            database.collection("USERS").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    age = documentSnapshot.getLong("age").intValue();
                    height = documentSnapshot.getLong("height").intValue();
                    weight = documentSnapshot.getDouble("weight");
                    vki = documentSnapshot.getDouble("vki");
                    username = documentSnapshot.getString("username");

                    if(ageList.contains(age))
                    {
                        int ageIndex = ageList.indexOf(age);
                        binding.ageSpinner.setSelection(ageIndex);
                    }
                    if(heightList.contains(height))
                    {
                        int heightIndex = heightList.indexOf(height);
                        binding.heightSpinner.setSelection(heightIndex);
                    }
                    binding.weightText2.setText(String.valueOf(weight));
                    binding.usernameText.setText(username);

                    vkiCalculator();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Verilerinize ulaşmakta zorluk çekiyoruz. Lütfen daha sonra tekrar deneyin.", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(requireContext(), "İnternet bağlantısı yok. Verilerinize ulaşamıyoruz.", Toast.LENGTH_SHORT).show();

        }


        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (internet != null && internet.isConnected()) //internetimiz varsa
                {
                    if(binding.ageSpinner.getSelectedItem() != null && !binding.ageSpinner.getSelectedItem().toString().isEmpty() && binding.heightSpinner.getSelectedItem() != null && !binding.heightSpinner.getSelectedItem().toString().isEmpty() && !binding.weightText2.getText().toString().isEmpty())
                    {
                        int ageValue = Integer.parseInt(binding.ageSpinner.getSelectedItem().toString());
                        int heightValue = Integer.parseInt(binding.heightSpinner.getSelectedItem().toString());
                        double weightValue = Double.parseDouble(binding.weightText2.getText().toString());


                        double heightMeters = heightValue / 100.0;
                        vki = weightValue / (heightMeters * heightMeters);

                        vkiCalculator();

                        database.collection("USERS").document(userID).update( //gerekli veri güncelleştirmeleri(key-value)
                                "age",ageValue,
                                "height",heightValue,
                                "weight",weightValue,
                                "vki", vki
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireContext(), "Veri başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Verilerinizi güncellemekte zorluk çekiyoruz. Lütfen daha sonra tekrar deneyin.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(requireContext(), "Lütfen tüm bilgileri doldurun.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(requireContext(), "İnternet bağlantısı yok. Verilerinizi güncelleyemiyoruz..", Toast.LENGTH_SHORT).show();


                }


            }
        });



        return binding.getRoot();
    }


    public void vkiCalculator()
    {
        if(vki > 0 && vki <= 18.4)
        {
            weightStatus = "Zayıf";
            binding.weightStatus.setTextColor(Color.parseColor("#81D4FA"));
            binding.cardVkiSonuc.setCardBackgroundColor(Color.parseColor("#81D4FA"));
        }
        else if(vki >= 18.5 && vki <=24.9)
        {
            weightStatus = "Normal";
            binding.weightStatus.setTextColor(Color.parseColor("#66BB6A"));
            binding.cardVkiSonuc.setCardBackgroundColor(Color.parseColor("#66BB6A"));
        }
        else if(vki >= 25.0 && vki <=29.9)
        {
            weightStatus = "Fazla Kilolu";
            binding.weightStatus.setTextColor(Color.parseColor("#FFEB3B"));
            binding.cardVkiSonuc.setCardBackgroundColor(Color.parseColor("#FFEB3B"));
        }
        else if(vki >= 30.0 && vki <=34.9)
        {
            weightStatus = "1. Derece Obez";
            binding.weightStatus.setTextColor(Color.parseColor("#FFA726"));
            binding.cardVkiSonuc.setCardBackgroundColor(Color.parseColor("#FFA726"));
        }
        else if(vki >= 35.0 && vki <=39.9)
        {
            weightStatus = "2. Derece Obez";
            binding.weightStatus.setTextColor(Color.parseColor("#EF5350"));
            binding.cardVkiSonuc.setCardBackgroundColor(Color.parseColor("#EF5350"));
        }
        else if (vki >= 40.0)
        {
            weightStatus = "3. Derece Obez";
            binding.weightStatus.setTextColor(Color.parseColor("#B71C1C"));
            binding.cardVkiSonuc.setCardBackgroundColor(Color.parseColor("#B71C1C"));
        }
        else
        {
            weightStatus = "Hatalı Giriş Yapıldı. Lütfen Tekrar Deneyiniz.";
            Toast.makeText(getContext(), "Vücut Kitle İndeksi verisi geçersiz. Yeniden deneyiniz.", Toast.LENGTH_SHORT).show();
            binding.weightStatus.setTextColor(Color.GRAY);

        }
        binding.weightStatus.setText(weightStatus);




    }



}