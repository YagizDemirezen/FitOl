package com.fitol.fitol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitol.FitOl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FoodFragment extends Fragment {

    private EditText editTextYemek;
    private Button buttonEkle;
    private TextView textViewToplamKalori;
    private RecyclerView recyclerViewYemekler;

    private final String API_KEY = "IZxoHMc2ct0ymFMxrLNDsmrCpr62VJC9G5Ky6AyN";

    private double toplamKalori = 0;

    private final ArrayList<String> yemekListesi = new ArrayList<>();
    private YemekAdapter yemekAdapter;

    public FoodFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        editTextYemek = view.findViewById(R.id.editTextYemek);
        buttonEkle = view.findViewById(R.id.buttonEkle);
        textViewToplamKalori = view.findViewById(R.id.textViewToplamKalori);
        recyclerViewYemekler = view.findViewById(R.id.recyclerViewYemekler);

        recyclerViewYemekler.setLayoutManager(new LinearLayoutManager(getContext()));
        yemekAdapter = new YemekAdapter(requireContext(), yemekListesi);
        recyclerViewYemekler.setAdapter(yemekAdapter);

        yemekAdapter.setOnItemRemovedListener(item -> {
            try {
                String[] parts = item.split("-");
                String kaloriStr = parts[1].replace("kcal", "").trim().split(" ")[0];
                double silinenKalori = Double.parseDouble(kaloriStr);
                toplamKalori -= silinenKalori;
                textViewToplamKalori.setText("Toplam Kalori: " + (int) toplamKalori + " Kcal");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        buttonEkle.setOnClickListener(v -> {
            String yemekAdi = editTextYemek.getText().toString().trim();
            editTextYemek.setText("");
            if (!yemekAdi.isEmpty()) {
                new Thread(() -> {
                    try {

                        String ceviri = TranslationAPI.translateText(yemekAdi); // Türkçe -> İngilizce

                        requireActivity().runOnUiThread(() -> getCaloriesFromApi(ceviri));
                    } catch (IOException error) {
                        error.printStackTrace();
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Çeviri hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Lütfen bir besin adı giriniz.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getCaloriesFromApi(String foodName) {
        OkHttpClient client = new OkHttpClient();

        String encodedFood;
        try {
            encodedFood = URLEncoder.encode(foodName, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Yemek adı kodlanamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + encodedFood;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "İnternet bağlantısı hatalı", Toast.LENGTH_SHORT).show());
                    Log.e("API_ERROR", e.getMessage(), e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Activity activity = getActivity();
                if (activity == null) return;

                String json = response.body().string().trim();

                try {
                    JSONObject root = new JSONObject(json);
                    JSONArray items = root.getJSONArray("items");

                    if (items.length() > 0) {
                        JSONObject food = items.getJSONObject(0);
                        double kalori = food.getDouble("calories");

                        String englishFoodName = foodName;

                        new Thread(() -> {
                            try {
                                String TRfoodName = TranslationAPI.translateTextToTR(englishFoodName); //ingilizce -> türkçe

                                if (activity != null) {
                                    activity.runOnUiThread(() -> {
                                        toplamKalori += kalori;

                                        String displayText = TRfoodName + " - " + (int) kalori + " kcal";
                                        yemekListesi.add(displayText);
                                        yemekAdapter.notifyItemInserted(yemekListesi.size() - 1);

                                        textViewToplamKalori.setText("Toplam Kalori: " + (int) toplamKalori + " Kcal");
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                if (activity != null) {
                                    activity.runOnUiThread(() -> Toast.makeText(getContext(), "Çeviri hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            }
                        }).start();

                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Besin bulunamadı.", Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> Toast.makeText(activity, "JSON ayrıştırma hatası.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
