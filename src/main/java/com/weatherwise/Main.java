package com.weatherwise;

import com.weatherwise.WeatherApi;

import java.io.IOException;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        try {
            // Force Windows to use UTF-8
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "chcp", "65001").inheritIO();
            pb.start().waitFor();
            
            Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8.name());
            System.out.println("Lütfen şehir ismi giriniz:");
            String city = sc.next();
            // Hava durumu verilerini almak istediğiniz şehri buraya girin
            String weatherData = WeatherApi.getWeatherData(city);

            // Gelen hava durumu verilerini kullanmak için burada işlemler yapabilirsiniz
            System.out.println(weatherData);
        } catch (IOException | InterruptedException e) {
            System.err.println("Hata: " + e.getMessage());
        }
    }
}
