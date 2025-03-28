package com.weatherwise.service;

import com.weatherwise.model.WeatherData;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WeatherService {
    private static final String API_KEY = "54c434b3cd01d84b68571aee3ac94d29";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final Map<String, WeatherData> cache = new HashMap<>();
    private static final long CACHE_DURATION = 300000; // 5 minutes in milliseconds

    public WeatherData getWeatherData(String city) throws IOException {
        // Check cache first
        if (cache.containsKey(city)) {
            WeatherData cachedData = cache.get(city);
            // TODO: Add timestamp to WeatherData and implement cache expiration
            return cachedData;
        }

        String apiUrl = String.format("%s?q=%s&appid=%s&units=metric&lang=en", BASE_URL, city, API_KEY);
        
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            WeatherData weatherData = parseWeatherData(jsonResponse);
            
            // Cache the result
            cache.put(city, weatherData);
            
            return weatherData;
        } else {
            throw new IOException("API request failed. Response Code: " + responseCode);
        }
    }

    private WeatherData parseWeatherData(JSONObject json) {
        JSONObject main = json.getJSONObject("main");
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        JSONObject sys = json.getJSONObject("sys");
        
        return new WeatherData(
            json.getString("name"),
            main.getDouble("temp"),
            main.getInt("humidity"),
            weather.getString("description"),
            json.getJSONObject("wind").getDouble("speed"),
            main.getInt("pressure"),
            main.getDouble("feels_like"),
            sys.getLong("sunrise"),
            sys.getLong("sunset"),
            sys.getString("country"),
            json.getInt("visibility")
        );
    }
} 