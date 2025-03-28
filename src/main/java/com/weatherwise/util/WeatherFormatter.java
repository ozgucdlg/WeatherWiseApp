package com.weatherwise.util;

import com.weatherwise.model.WeatherData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class WeatherFormatter {
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    public static String formatWeatherData(WeatherData data) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("📍 %s, %s\n\n", data.getCityName(), data.getCountry()));
        sb.append(String.format("🌡️ Temperature: %.1f°C\n", data.getTemperature()));
        sb.append(String.format("🌡️ Feels like: %.1f°C\n", data.getFeelsLike()));
        sb.append(String.format("💧 Humidity: %d%%\n", data.getHumidity()));
        sb.append(String.format("🌪️ Wind Speed: %.1f m/s\n", data.getWindSpeed()));
        sb.append(String.format("⏲️ Pressure: %d hPa\n", data.getPressure()));
        sb.append(String.format("👁️ Visibility: %.1f km\n\n", data.getVisibility() / 1000.0));
        
        sb.append(String.format("☀️ Sunrise: %s\n", formatTime(data.getSunrise())));
        sb.append(String.format("🌙 Sunset: %s\n\n", formatTime(data.getSunset())));
        
        sb.append(String.format("📝 Condition: %s", capitalizeFirst(data.getDescription())));
        
        return sb.toString();
    }
    
    private static String formatTime(long timestamp) {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp),
            ZoneId.systemDefault()
        ).format(TIME_FORMATTER);
    }
    
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + 
               str.substring(1).toLowerCase(Locale.ENGLISH);
    }
} 