package com.weatherwise.config;

public class AppConfig {
    // Database configuration - Using SQLite for development
    public static final String DATABASE_URL = "jdbc:sqlite:weatherwise_auth.db";
    public static final String DATABASE_USERNAME = "";
    public static final String DATABASE_PASSWORD = "";
    public static final String DATABASE_DRIVER = "org.sqlite.JDBC";
    
    // Connection pool configuration (not used for SQLite)
    public static final int MAX_POOL_SIZE = 1;
    public static final int MIN_IDLE_CONNECTIONS = 1;
    public static final long CONNECTION_TIMEOUT_MS = 30000;
    public static final long IDLE_TIMEOUT_MS = 600000;
    public static final long MAX_LIFETIME_MS = 1800000;
    
    // Session configuration
    public static final long SESSION_TIMEOUT_MINUTES = 30;
    public static final long SESSION_CLEANUP_INTERVAL_MINUTES = 5;
    
    // Security configuration
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 20;
    
    // UI configuration
    public static final int LOGIN_WINDOW_WIDTH = 400;
    public static final int LOGIN_WINDOW_HEIGHT = 500;
    public static final int SIGNUP_WINDOW_WIDTH = 450;
    public static final int SIGNUP_WINDOW_HEIGHT = 600;
    public static final int MAIN_WINDOW_WIDTH = 600;
    public static final int MAIN_WINDOW_HEIGHT = 550;
    
    // Weather API configuration
    public static final String WEATHER_API_KEY = "54c434b3cd01d84b68571aee3ac94d29";
    public static final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    public static final String WEATHER_UNITS = "metric";
    public static final String WEATHER_LANGUAGE = "en";
    
    // Cache configuration
    public static final long WEATHER_CACHE_DURATION_MS = 300000; // 5 minutes
    
    // Application information
    public static final String APP_NAME = "WeatherWise";
    public static final String APP_VERSION = "2.0.0";
    public static final String APP_DESCRIPTION = "A modern weather application with user authentication";
    
    private AppConfig() {
        // Utility class - prevent instantiation
    }
} 