package com.fitol.fitol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslationAPI {
    public static String translateText(String text) throws IOException {
        String apiUrl = "https://script.google.com/macros/s/AKfycby2kKP20Wb4YA0nyLtv6xzIyDLNhN8ZtX3sm6HrDwMpkxMHxf01zgu4zsM3uXDIbTLtlg/exec"; //apim yagiz
        String query = "?text=" + URLEncoder.encode(text, "UTF-8") + "&source=tr&target=en";

        URL url = new URL(apiUrl + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();

        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        return response.toString();
    }

    public static String translateTextToTR(String text) throws IOException {
        String apiUrl = "https://script.google.com/macros/s/AKfycby2kKP20Wb4YA0nyLtv6xzIyDLNhN8ZtX3sm6HrDwMpkxMHxf01zgu4zsM3uXDIbTLtlg/exec"; //apim yagiz
        String query = "?text=" + URLEncoder.encode(text, "UTF-8") + "&source=en&target=tr";

        URL url = new URL(apiUrl + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();

        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        return response.toString();
    }
}
