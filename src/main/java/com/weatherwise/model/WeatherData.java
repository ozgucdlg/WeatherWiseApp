package com.weatherwise.model;

import com.weatherwise.util.WeatherFormatter;

public class WeatherData {
    private String cityName;
    private double temperature;
    private int humidity;
    private String description;
    private double windSpeed;
    private int pressure;
    private double feelsLike;
    private long sunrise;
    private long sunset;
    private String country;
    private int visibility;

    // Constructor
    public WeatherData(String cityName, double temperature, int humidity, 
                      String description, double windSpeed, int pressure,
                      double feelsLike, long sunrise, long sunset, 
                      String country, int visibility) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.feelsLike = feelsLike;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.country = country;
        this.visibility = visibility;
    }

    // Getters
    public String getCityName() { return cityName; }
    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public String getDescription() { return description; }
    public double getWindSpeed() { return windSpeed; }
    public int getPressure() { return pressure; }
    public double getFeelsLike() { return feelsLike; }
    public long getSunrise() { return sunrise; }
    public long getSunset() { return sunset; }
    public String getCountry() { return country; }
    public int getVisibility() { return visibility; }

    @Override
    public String toString() {
        return WeatherFormatter.formatWeatherData(this);
    }
} 