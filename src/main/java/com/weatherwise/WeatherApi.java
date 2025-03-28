package com.weatherwise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class WeatherApi {
    public static String getWeatherData(String city) throws IOException {
        String apiKey = "54c434b3cd01d84b68571aee3ac94d29";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric&lang=tr";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            
            // Extract relevant weather information
            JSONObject main = jsonResponse.getJSONObject("main");
            JSONObject weather = jsonResponse.getJSONArray("weather").getJSONObject(0);
            
            return String.format("Şehir: %s%nSıcaklık: %.1f°C%nNem: %d%%%nHava Durumu: %s",
                jsonResponse.getString("name"),
                main.getDouble("temp"),
                main.getInt("humidity"),
                weather.getString("description"));
        } else {
            throw new IOException("API isteği başarısız oldu. Yanıt Kodu: " + responseCode);
        }
    }
}

